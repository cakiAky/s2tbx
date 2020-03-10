package org.esa.s1tbx.commons.test;

import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.engine_utilities.datamodel.AbstractMetadata;
import org.esa.snap.engine_utilities.gpf.InputProductValidator;

public class MetadataValidator {

    private final Product product;
    private final MetadataElement absRoot;

    private final static String defStr = AbstractMetadata.NO_METADATA_STRING;
    private final static int defInt = AbstractMetadata.NO_METADATA;

    public MetadataValidator(final Product product) {
        this.product = product;
        this.absRoot = AbstractMetadata.getAbstractedMetadata(product);
    }

    public void validate() throws Exception {
//        final MetadataAttribute[] attribs = absRoot.getAttributes();
//        for(MetadataAttribute attrib : attribs) {
//            System.out.println(attrib.getName() +" = "+ attrib.getData().toString());
//        }

        verifyStr(AbstractMetadata.PRODUCT);
        verifyStr(AbstractMetadata.PRODUCT_TYPE);
        verifyStr(AbstractMetadata.MISSION);
        verifyStr(AbstractMetadata.ACQUISITION_MODE);
        verifyStr(AbstractMetadata.SPH_DESCRIPTOR);
        verifyStr(AbstractMetadata.PASS);

        final InputProductValidator validator = new InputProductValidator(product);
        if(validator.isSARProduct()) {
            validateSAR();
        } else {
            validateOptical();
        }
    }

    public void validateOptical() throws Exception {
        throw new Exception("Is this SAR");
    }

    public void validateSAR() throws Exception {

        verifyStr(AbstractMetadata.SAMPLE_TYPE);

        verifyDouble(AbstractMetadata.radar_frequency);
        verifyDouble(AbstractMetadata.pulse_repetition_frequency);
        verifyDouble(AbstractMetadata.range_spacing);
        verifyDouble(AbstractMetadata.azimuth_spacing);
        verifyDouble(AbstractMetadata.range_looks);
        verifyDouble(AbstractMetadata.azimuth_looks);
        verifyDouble(AbstractMetadata.line_time_interval);
        verifyDouble(AbstractMetadata.slant_range_to_first_pixel);

        verifyInt(AbstractMetadata.num_output_lines);
        verifyInt(AbstractMetadata.num_samples_per_line);

        //verifySRGR();
        //verifyOrbitStateVectors();
        //verifyDopplerCentroids();
    }

    private void verifySRGR() throws Exception {
        final MetadataElement srgrElem = absRoot.getElement(AbstractMetadata.srgr_coefficients);
        if(srgrElem != null) {
            MetadataElement[] elems = srgrElem.getElements();
            if(elems.length == 0) {
                throw new Exception("SRGR Coefficients not found");
            }
            MetadataElement coefList = elems[0];
            if(!coefList.containsAttribute(AbstractMetadata.srgr_coef_time)) {
                throw new Exception("SRGR "+AbstractMetadata.srgr_coef_time+" not found");
            }
            if(!coefList.containsAttribute(AbstractMetadata.ground_range_origin)) {
                throw new Exception("SRGR "+AbstractMetadata.ground_range_origin+" not found");
            }

            MetadataElement[] srgrList = coefList.getElements();
            if(srgrList.length == 0) {
                throw new Exception("SRGR Coefficients not found");
            }
            MetadataElement srgr = srgrList[0];
            if(!srgr.containsAttribute(AbstractMetadata.srgr_coef)) {
                throw new Exception("SRGR "+AbstractMetadata.srgr_coef+" not found");
            }
        } else {
            throw new Exception("SRGR Coefficients not found");
        }
    }

    private void verifyOrbitStateVectors() throws Exception {
        final MetadataElement orbitElem = absRoot.getElement(AbstractMetadata.orbit_state_vectors);
        if(orbitElem != null) {
            MetadataElement[] elems = orbitElem.getElements();
            if(elems.length == 0) {
                throw new Exception("Orbit State Vectors not found");
            }
        } else {
            throw new Exception("Orbit State Vectors not found");
        }
    }

    private void verifyDopplerCentroids() throws Exception {
        final MetadataElement dopElem = absRoot.getElement(AbstractMetadata.dop_coefficients);
        if(dopElem != null) {
            MetadataElement[] elems = dopElem.getElements();
            if(elems.length == 0) {
                throw new Exception("Doppler Centroids not found");
            }
            MetadataElement coefList = elems[0];
            if(!coefList.containsAttribute(AbstractMetadata.dop_coef_time)) {
                throw new Exception("Doppler Centroids "+AbstractMetadata.dop_coef_time+" not found");
            }
            if(!coefList.containsAttribute(AbstractMetadata.slant_range_time)) {
                throw new Exception("Doppler Centroids "+AbstractMetadata.slant_range_time+" not found");
            }
        } else {
            throw new Exception("Doppler Centroids not found");
        }
    }

    private void verifyStr(final String tag) throws Exception {
        String value = absRoot.getAttributeString(tag);
        if(value == null || value.trim().isEmpty() || value.equals(defStr)) {
            throw new Exception("Metadata " + tag + " is invalid " + value);
        }
    }

    private void verifyDouble(final String tag) throws Exception {
        Double value = absRoot.getAttributeDouble(tag);
        if(value == null || value.equals(defInt)) {
            throw new Exception("Metadata " + tag + " is invalid " + value);
        }
    }

    private void verifyInt(final String tag) throws Exception {
        Integer value = absRoot.getAttributeInt(tag);
        if(value == null || value.equals(defInt)) {
            throw new Exception("Metadata " + tag + " is invalid " + value);
        }
    }
}