package ngsdiaglim.utils;

import ngsdiaglim.modeles.variants.Variant;

public class VariantUtils {

    /**
     *
     * @return The hash of a variant ex : chr1:123456A>C
     */
    public static String getHashVariant(Variant vc) {
        return vc.getContig() +
                ":" +
                vc.getStart() +
                vc.getRef() +
                ">" +
                vc.getAlt();
    }
}
