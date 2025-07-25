package nl.jqno.equalsverifier.internal.checkers;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.NullPointerExceptionFieldCheck;
import nl.jqno.equalsverifier.internal.util.Context;

public class NullChecker<T> implements Checker {

    private final Context<T> context;

    public NullChecker(Context<T> context) {
        this.context = context;
    }

    @Override
    public void check() {
        if (context.getConfiguration().warningsToSuppress().contains(Warning.NULL_FIELDS)) {
            return;
        }

        var inspector = new FieldInspector<>(context.getType(), context.getConfiguration().isKotlin());
        inspector.check(new NullPointerExceptionFieldCheck<>(context));
    }
}
