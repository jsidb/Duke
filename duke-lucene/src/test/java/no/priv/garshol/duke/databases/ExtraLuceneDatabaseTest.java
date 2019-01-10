
package no.priv.garshol.duke.databases;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import no.priv.garshol.duke.ConfigurationImpl;
import no.priv.garshol.duke.DukeException;
import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.PropertyImpl;
import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.RecordImpl;

public class ExtraLuceneDatabaseTest {

	@Test
	public void testLockedIndex() throws IOException {
		// this test verifies that we don't wind up in an inconsistent state
		// when the index we want to work with is already locked

		// make a locked index
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
		Path tmp = Paths.get(System.getProperty("java.io.tmpdir"), "lucene-temp-" + (Math.random() * 100000));

		Directory directory = FSDirectory.open(tmp);
		IndexWriter writer = new IndexWriter(directory, cfg);

		// now try to open a LuceneDatabase in the same place
		List<Property> properties = new ArrayList();
		properties.add(new PropertyImpl("id"));
		ConfigurationImpl config = new ConfigurationImpl();
		config.setProperties(properties);
		LuceneDatabase db = new LuceneDatabase();
		db.setPath(tmp.toAbsolutePath().toString());
		db.setConfiguration(config);

		Record r = new RecordImpl();
		try {
			db.index(r);
		} catch (DukeException e) {
			// this is expected
		}

		try {
			db.index(r);
		} catch (DukeException e) {
			// this is also expected
		}
	}

}
