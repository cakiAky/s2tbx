package org.esa.s2tbx.dataio.s2.l3;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.l3.metadata.L3Metadata;
import org.esa.s2tbx.dataio.s2.l3.metadata.S2L3ProductMetadataReader;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by obarrile on 15/06/2016.
 */
public class Sentinel2L3ProductReader  extends Sentinel2OrthoProductReader {

    static final String L3_CACHE_DIR = "l3-reader";

    public Sentinel2L3ProductReader(ProductReaderPlugIn readerPlugIn, String epsgCode) {
        super(readerPlugIn, epsgCode);
    }

    @Override
    protected S2L3ProductMetadataReader buildProductMetadata(VirtualPath virtualPath) throws IOException {
        return new S2L3ProductMetadataReader(virtualPath, this.epsgCode, getProductResolution());
    }

    @Override
    public S2SpatialResolution getProductResolution() {
        if(namingConvention == null && (getInput() instanceof File)) {
            try {
                namingConvention = NamingConventionFactory.createOrthoNamingConvention(S2NamingConventionUtils.transformToSentinel2VirtualPath(((File) getInput()).toPath()));
            } catch (IOException e) {
                return S2SpatialResolution.R10M;
            }
        }

        if(namingConvention == null) {
            return S2SpatialResolution.R10M;
        }

        return namingConvention.getResolution();
    }

    @Override
    protected String getReaderCacheDir() {
        return L3_CACHE_DIR;
    }

    @Override
    protected S2Metadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsg, boolean isAGranule) throws IOException {
        try {
            return L3Metadata.parseHeader(path, granuleName, config, epsg, getProductResolution(), isAGranule, namingConvention);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to parse metadata in " + path.getFileName().toString());
        }
    }

    @Override
    protected String getImagePathString(String imageFileName, S2SpatialResolution resolution) {
        String resolutionFolder = String.format("R%dm", resolution.resolution);
        String imageWithoutExtension = imageFileName.substring(0, imageFileName.length() - 4);
        return String.format("%s%s%s_%dm.jp2",
                             resolutionFolder,
                             File.separator,
                             imageWithoutExtension,
                             resolution.resolution);
    }

    @Override
    protected int getMaskLevel() {
        return MaskInfo.L3;
    }
}
