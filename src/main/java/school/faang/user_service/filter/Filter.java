package school.faang.user_service.filter;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

public interface Filter<F, E> {
    boolean isApplicable(F f);

    void apply(List<E> eList, F f);
}
