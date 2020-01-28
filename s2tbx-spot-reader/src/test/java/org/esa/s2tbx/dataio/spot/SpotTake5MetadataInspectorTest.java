package org.esa.s2tbx.dataio.spot;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Created by jcoravu on 21/1/2020.
 */
public class SpotTake5MetadataInspectorTest {

    private static final String PRODUCTS_FOLDER = "_spot" + File.separator;

    public SpotTake5MetadataInspectorTest() {
    }

    @Test
    public void testMetadataInspector() throws URISyntaxException, IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "SPOT4_HRVIR1_XS_88888888_N1A.xml");

        SpotTake5MetadataInspector metadataInspector = new SpotTake5MetadataInspector();
        MetadataInspector.Metadata metadata = metadataInspector.getMetadata(productFile.toPath());
        assertNotNull(metadata);
        assertEquals(4000, metadata.getProductWidth());
        assertEquals(3750, metadata.getProductHeight());

        GeoCoding geoCoding = metadata.getGeoCoding();
        assertNull(geoCoding);

        assertNotNull(metadata.getBandList());
        assertEquals(4, metadata.getBandList().size());
        assertTrue(metadata.getBandList().contains("XS1"));
        assertTrue(metadata.getBandList().contains("XS2"));
        assertTrue(metadata.getBandList().contains("XS3"));
        assertTrue(metadata.getBandList().contains("SWIR"));

        assertNotNull(metadata.getMaskList());
        assertEquals(0, metadata.getMaskList().size());
    }
}
