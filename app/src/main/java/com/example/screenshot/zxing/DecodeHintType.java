package com.example.screenshot.zxing;
import java.util.List;

public enum DecodeHintType {


    OTHER(Object.class),


    PURE_BARCODE(Void.class),

    /**
     * Image is known to be of one of a few possible formats.
     * Maps to a {@link List} of {@link BarcodeFormat}s.
     */
    POSSIBLE_FORMATS(List.class),


    TRY_HARDER(Void.class),

    /**
     * Specifies what character encoding to use when decoding, where applicable (type String)
     */
    CHARACTER_SET(String.class),

    /**
     * Allowed lengths of encoded data -- reject anything else. Maps to an {@code int[]}.
     */
    ALLOWED_LENGTHS(int[].class),


    ASSUME_CODE_39_CHECK_DIGIT(Void.class),


    ASSUME_GS1(Void.class),


    RETURN_CODABAR_START_END(Void.class),

    /**
     * The caller needs to be notified via callback when a possible {@link ResultPoint}
     * is found. Maps to a {@link ResultPointCallback}.
     */
    NEED_RESULT_POINT_CALLBACK(ResultPointCallback.class),


    /**
     * Allowed extension lengths for EAN or UPC barcodes. Other formats will ignore this.
     * Maps to an {@code int[]} of the allowed extension lengths, for example [2], [5], or [2, 5].
     * If it is optional to have an extension, do not set this hint. If this is set,
     * and a UPC or EAN barcode is found but an extension is not, then no result will be returned
     * at all.
     */
    ALLOWED_EAN_EXTENSIONS(int[].class),

    // End of enumeration values.
    ;


    private final Class<?> valueType;

    DecodeHintType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

}

