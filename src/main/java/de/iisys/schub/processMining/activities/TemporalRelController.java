package de.iisys.schub.processMining.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.iisys.schub.processMining.activities.model.TaskCandidate;

public class TemporalRelController {
	
	public static List<Set<TaskCandidate>> sortByChapters(Set<TaskCandidate> tcSet) {
		List<Set<TaskCandidate>> listSet = new ArrayList<Set<TaskCandidate>>();
		List<String> chapters = new ArrayList<String>();
		
		for(TaskCandidate tc : tcSet) {
			int index = chapters.indexOf(tc.getSimilarChapteTitle());
			if(index == -1) {
				chapters.add(tc.getSimilarChapteTitle());
				index = chapters.size()-1;
				listSet.add(index, new HashSet<TaskCandidate>());
			}
			
			listSet.get(index).add(tc);
		}
		
		return listSet;
	}
	
	public static void findTemporalSuccessors(Set<TaskCandidate> tcSet) {
		System.out.println("Calculating temporal taskCandidate relations...");
		for(TaskCandidate tc : tcSet) {
			TreeSet<Date> publishedDates = tc.getPublishedDates();
			Date lowestDate = null;
			Date highestDate = null;
			
			if(publishedDates.size()>1) {
				lowestDate = publishedDates.first();
				highestDate = publishedDates.last();
//			} else if(publishedDates.size()==1) {
//				for(Date tempDate : publishedDates) {
//					lowestDate = highestDate = tempDate;
//				}
			} else {
				continue;
			}
			System.out.println("\tcompare tc...");
			
			for(TaskCandidate tcCompare : tcSet) {
				if(tc==tcCompare) {
					System.out.println("\ttc==tcCompare");
					continue;
				}
				Date lowestCompare = null;
				Date highestCompare = null;
				
				if(tcCompare.getPublishedDates().size()>1) {
					lowestCompare = tcCompare.getPublishedDates().first();
					highestCompare = tcCompare.getPublishedDates().last();
//				} else if(tcCompare.getPublishedDates().size()==1) {
//					for(Date tempDate : tcCompare.getPublishedDates()) {
//						lowestCompare = highestCompare = tempDate;
//					}
				} else {
					continue;
				}
				
				if(highestDate.before(lowestCompare)) {
					// all tc dates are before tcCompare's dates
					tc.addSuccessor(tcCompare);
					System.out.println("\ttc has successor");
				} else if(lowestDate.after(highestCompare)) {
					// all tc dates are after tcCompare's dates
					tcCompare.addSuccessor(tc);
					System.out.println("\ttc has predecessor");
				} else {
					System.out.println("\ttc and tcCompare have mixed dates.");
				}
			}
		}
		System.out.println("Done!");
	}
}
