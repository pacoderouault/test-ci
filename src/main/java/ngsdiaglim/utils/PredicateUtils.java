package ngsdiaglim.utils;

import ngsdiaglim.modeles.variants.Annotation;
//import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class PredicateUtils {

    @SafeVarargs
    public static Predicate<Annotation> addPredicates(Predicate<Annotation>... predicates) {
       return Arrays.stream(predicates).filter(Objects::nonNull).reduce(x->true, Predicate::and);
    }
}

