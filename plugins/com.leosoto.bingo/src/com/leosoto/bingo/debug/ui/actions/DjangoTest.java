package com.leosoto.bingo.debug.ui.actions;
import org.eclipse.jface.action.IAction;

public class DjangoTest extends DjangoAction {

    public void run(IAction action) {
    	launchDjangoCommand("test");
    }

}
