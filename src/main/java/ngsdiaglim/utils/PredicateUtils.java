package ngsdiaglim.utils;

import ngsdiaglim.modeles.variants.Annotation;
//import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class PredicateUtils {

//    public static Predicate<?> addPredicate(Predicate<?> p1, Predicate<?> p2) {
//        if(p1 == null && p2 == null) {
//            return null;
//        } else if (p1 == null) {
//            return p2;
//        } else if (p2 == null) {
//            return p1;
//        } else {
//            return p1.and(p2);
//        }
//    }

    @SafeVarargs
    public static Predicate<Annotation> addPredicates(Predicate<Annotation>... predicates) {
       return Arrays.stream(predicates).filter(Objects::nonNull).reduce(x->true, Predicate::and);
//        Predicate<Object> predicate = null;
//        for (Predicate<Object> p : predicates) {
//            if (p != null) {
//                if (predicate == null) predicate = p;
//                else predicate = predicate.and(p);
//            }
//        }
//        return predicate;
    }
}

