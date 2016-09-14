package de.iisys.schub.processMining.similarity;

import java.util.List;

import de.iisys.schub.processMining.similarity.model.Similarity;

/**
 * Cosine similarity calculator class.
 *
 * @author Mubin Shrestha
 *		Source: http://computergodzilla.blogspot.de/2013/07/how-to-calculate-tf-idf-of-document.html 
 * @author Christian Ochsenkühn
 */
public class CosineSimilarity {

    /**
     * Method to calculate cosine similarity between two documents.
     * Saves the three most similar terms between those documents.
     * 
     * @author Christian Ochsenkühn
     * @param docVector1 : document vector 1
     * @param docVector2 : document vector 2
     * @return 
     * 		Returns the cosine similarity and the three similar terms as an object.
     */
    public static Similarity getCosineSimilarityAndSimilarTerms(double[] docVector1, double[] docVector2, List<String> allTerms) {
        if(docVector1==null || docVector2==null) {
        	return null;
        } else {
	    	double dotProduct = 0.0;
	        double magnitude1 = 0.0;
	        double magnitude2 = 0.0;
	        double cosineSimilarity = 0.0;
	        
	        double dotProductTemp;
	        
	        int highestDot1 = 0;
	        double highestDotValue1 = 0;
	        int highestDot2 = 0;
	        double highestDotValue2 = 0;
	        int highestDot3 = 0;
	        double highestDotValue3 = 0;
	
	        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
	        {
	        	dotProductTemp = docVector1[i] * docVector2[i];  //a.b
	        	dotProduct += dotProductTemp;
	            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
	            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
	            
	            
	            if(dotProductTemp > highestDotValue1) {
	            	highestDotValue3 = highestDotValue2;
	            	highestDot3 = highestDot2;
	            	
	            	highestDotValue2 = highestDotValue1;
	            	highestDot2 = highestDot1;
	            	
	            	highestDotValue1 = dotProductTemp;
	            	highestDot1 = i;
	            } else if(dotProductTemp > highestDotValue2) {
	            	highestDotValue3 = highestDotValue2;
	            	highestDot3 = highestDot2;
	            	
	            	highestDotValue2 = dotProductTemp;
	            	highestDot2 = i;
	            } else if(dotProductTemp > highestDotValue3) {
	            	highestDotValue3 = dotProductTemp;
	            	highestDot3 = i;
	            }
	        }
	        
	        String[] importantTerms = {allTerms.get(highestDot1),
	        							allTerms.get(highestDot2),
	        							allTerms.get(highestDot3)};
	        /*
	        System.out.println("Nearest term 1: "+allTerms.get(highestDot1)+" ("+highestDotValue1+")");
	        System.out.println("Nearest term 2: "+allTerms.get(highestDot2)+" ("+highestDotValue2+")");
	        System.out.println("Nearest term 3: "+allTerms.get(highestDot3)+" ("+highestDotValue3+")"+"\n\n"); */
	
	        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
	        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
	
	        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
	            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
	        } else {
	        	cosineSimilarity = 0.0;
	        }
	
	
	        return new Similarity(cosineSimilarity, importantTerms);
        }
    }
    
    
    /**
     * Method to calculate cosine similarity between two documents.
     * @author Mubin Shrestha
     * @param docVector1 : document vector 1 (a)
     * @param docVector2 : document vector 2 (b)
     * @return 
     */
    public static double getCosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1[i] * docVector2[i];  //a.b
            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
        return cosineSimilarity;
    }
    
}
