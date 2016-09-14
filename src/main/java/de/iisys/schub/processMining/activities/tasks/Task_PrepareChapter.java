package de.iisys.schub.processMining.activities.tasks;

/**
 * Not used.
 *
 */
public class Task_PrepareChapter implements ITaskContainer {

	@Override
	public String createTask(String[] args) {
		if(args.length >= 3)
			return "Prepare chapter " + args[0] + " in a " + args[1] + " together with " + args[2];
		else
			return null;
	}

}
