package de.iisys.schub.processMining.similarity.stopwords;

/**
 * Class to check for stopwords (German).
 * Used stopwords from:
 * http://members.unine.ch/jacques.savoy/clef/germanST.txt (BSD 2-Clause License)
 * 
 * To create/use own stopwords (with the proper syntax) you can use the StopwordsGeneratorMain class.
 * 
 * 
 * LICENSE:
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Christian Ochsenk�hn
 *
 */
public class StopwordsGerman extends IStopwords {
	
	public StopwordsGerman() {
		super();
	}

	@Override
	protected void init() {
		stopwords.put("a", x);
		stopwords.put("ab", x);
		stopwords.put("aber", x);
		stopwords.put("aber", x);
		stopwords.put("ach", x);
		stopwords.put("acht", x);
		stopwords.put("achte", x);
		stopwords.put("achten", x);
		stopwords.put("achter", x);
		stopwords.put("achtes", x);
		stopwords.put("ag", x);
		stopwords.put("alle", x);
		stopwords.put("allein", x);
		stopwords.put("allem", x);
		stopwords.put("allen", x);
		stopwords.put("aller", x);
		stopwords.put("allerdings", x);
		stopwords.put("alles", x);
		stopwords.put("allgemeinen", x);
		stopwords.put("als", x);
		stopwords.put("als", x);
		stopwords.put("also", x);
		stopwords.put("am", x);
		stopwords.put("an", x);
		stopwords.put("andere", x);
		stopwords.put("anderen", x);
		stopwords.put("andern", x);
		stopwords.put("anders", x);
		stopwords.put("au", x);
		stopwords.put("auch", x);
		stopwords.put("auch", x);
		stopwords.put("auf", x);
		stopwords.put("aus", x);
		stopwords.put("ausser", x);
		stopwords.put("au�er", x);
		stopwords.put("ausserdem", x);
		stopwords.put("au�erdem", x);
		
		stopwords.put("b", x);
		stopwords.put("bald", x);
		stopwords.put("bei", x);
		stopwords.put("beide", x);
		stopwords.put("beiden", x);
		stopwords.put("beim", x);
		stopwords.put("beispiel", x);
		stopwords.put("bekannt", x);
		stopwords.put("bereits", x);
		stopwords.put("besonders", x);
		stopwords.put("besser", x);
		stopwords.put("besten", x);
		stopwords.put("bin", x);
		stopwords.put("bis", x);
		stopwords.put("bisher", x);
		stopwords.put("bist", x);
		
		stopwords.put("c", x);
		
		stopwords.put("d", x);
		stopwords.put("da", x);
		stopwords.put("dabei", x);
		stopwords.put("dadurch", x);
		stopwords.put("daf�r", x);
		stopwords.put("dagegen", x);
		stopwords.put("daher", x);
		stopwords.put("dahin", x);
		stopwords.put("dahinter", x);
		stopwords.put("damals", x);
		stopwords.put("damit", x);
		stopwords.put("danach", x);
		stopwords.put("daneben", x);
		stopwords.put("dank", x);
		stopwords.put("dann", x);
		stopwords.put("daran", x);
		stopwords.put("darauf", x);
		stopwords.put("daraus", x);
		stopwords.put("darf", x);
		stopwords.put("darfst", x);
		stopwords.put("darin", x);
		stopwords.put("dar�ber", x);
		stopwords.put("darum", x);
		stopwords.put("darunter", x);
		stopwords.put("das", x);
		stopwords.put("das", x);
		stopwords.put("dasein", x);
		stopwords.put("daselbst", x);
		stopwords.put("dass", x);
		stopwords.put("da�", x);
		stopwords.put("dasselbe", x);
		stopwords.put("davon", x);
		stopwords.put("davor", x);
		stopwords.put("dazu", x);
		stopwords.put("dazwischen", x);
		stopwords.put("dein", x);
		stopwords.put("deine", x);
		stopwords.put("deinem", x);
		stopwords.put("deiner", x);
		stopwords.put("dem", x);
		stopwords.put("dementsprechend", x);
		stopwords.put("demgegen�ber", x);
		stopwords.put("demgem�ss", x);
		stopwords.put("demgem��", x);
		stopwords.put("demselben", x);
		stopwords.put("demzufolge", x);
		stopwords.put("den", x);
		stopwords.put("denen", x);
		stopwords.put("denn", x);
		stopwords.put("denn", x);
		stopwords.put("denselben", x);
		stopwords.put("der", x);
		stopwords.put("deren", x);
		stopwords.put("derjenige", x);
		stopwords.put("derjenigen", x);
		stopwords.put("dermassen", x);
		stopwords.put("derma�en", x);
		stopwords.put("derselbe", x);
		stopwords.put("derselben", x);
		stopwords.put("des", x);
		stopwords.put("deshalb", x);
		stopwords.put("desselben", x);
		stopwords.put("dessen", x);
		stopwords.put("deswegen", x);
		stopwords.put("d.h", x);
		stopwords.put("dich", x);
		stopwords.put("die", x);
		stopwords.put("diejenige", x);
		stopwords.put("diejenigen", x);
		stopwords.put("dies", x);
		stopwords.put("diese", x);
		stopwords.put("dieselbe", x);
		stopwords.put("dieselben", x);
		stopwords.put("diesem", x);
		stopwords.put("diesen", x);
		stopwords.put("dieser", x);
		stopwords.put("dieses", x);
		stopwords.put("dir", x);
		stopwords.put("doch", x);
		stopwords.put("dort", x);
		stopwords.put("drei", x);
		stopwords.put("drin", x);
		stopwords.put("dritte", x);
		stopwords.put("dritten", x);
		stopwords.put("dritter", x);
		stopwords.put("drittes", x);
		stopwords.put("du", x);
		stopwords.put("durch", x);
		stopwords.put("durchaus", x);
		stopwords.put("d�rfen", x);
		stopwords.put("d�rft", x);
		stopwords.put("durfte", x);
		stopwords.put("durften", x);
		
		stopwords.put("e", x);
		stopwords.put("eben", x);
		stopwords.put("ebenso", x);
		stopwords.put("ehrlich", x);
		stopwords.put("ei", x);
		stopwords.put("ei,", x);
		stopwords.put("ei,", x);
		stopwords.put("eigen", x);
		stopwords.put("eigene", x);
		stopwords.put("eigenen", x);
		stopwords.put("eigener", x);
		stopwords.put("eigenes", x);
		stopwords.put("ein", x);
		stopwords.put("einander", x);
		stopwords.put("eine", x);
		stopwords.put("einem", x);
		stopwords.put("einen", x);
		stopwords.put("einer", x);
		stopwords.put("eines", x);
		stopwords.put("einige", x);
		stopwords.put("einigen", x);
		stopwords.put("einiger", x);
		stopwords.put("einiges", x);
		stopwords.put("einmal", x);
		stopwords.put("einmal", x);
		stopwords.put("eins", x);
		stopwords.put("elf", x);
		stopwords.put("en", x);
		stopwords.put("ende", x);
		stopwords.put("endlich", x);
		stopwords.put("entweder", x);
		stopwords.put("entweder", x);
		stopwords.put("er", x);
		stopwords.put("Ernst", x);
		stopwords.put("erst", x);
		stopwords.put("erste", x);
		stopwords.put("ersten", x);
		stopwords.put("erster", x);
		stopwords.put("erstes", x);
		stopwords.put("es", x);
		stopwords.put("etwa", x);
		stopwords.put("etwas", x);
		stopwords.put("euch", x);
		
		stopwords.put("f", x);
		stopwords.put("frau", x);
		stopwords.put("fr�her", x);
		stopwords.put("f�nf", x);
		stopwords.put("f�nfte", x);
		stopwords.put("f�nften", x);
		stopwords.put("f�nfter", x);
		stopwords.put("f�nftes", x);
		stopwords.put("f�r", x);
		
		stopwords.put("g", x);
		stopwords.put("gab", x);
		stopwords.put("ganz", x);
		stopwords.put("ganze", x);
		stopwords.put("ganzen", x);
		stopwords.put("ganzer", x);
		stopwords.put("ganzes", x);
		stopwords.put("gar", x);
		stopwords.put("gedurft", x);
		stopwords.put("gegen", x);
		stopwords.put("gegen�ber", x);
		stopwords.put("gehabt", x);
		stopwords.put("gehen", x);
		stopwords.put("geht", x);
		stopwords.put("gekannt", x);
		stopwords.put("gekonnt", x);
		stopwords.put("gemacht", x);
		stopwords.put("gemocht", x);
		stopwords.put("gemusst", x);
		stopwords.put("genug", x);
		stopwords.put("gerade", x);
		stopwords.put("gern", x);
		stopwords.put("gesagt", x);
		stopwords.put("gesagt", x);
		stopwords.put("geschweige", x);
		stopwords.put("gewesen", x);
		stopwords.put("gewollt", x);
		stopwords.put("geworden", x);
		stopwords.put("gibt", x);
		stopwords.put("ging", x);
		stopwords.put("gleich", x);
		stopwords.put("gott", x);
		stopwords.put("gross", x);
		stopwords.put("gro�", x);
		stopwords.put("grosse", x);
		stopwords.put("gro�e", x);
		stopwords.put("grossen", x);
		stopwords.put("gro�en", x);
		stopwords.put("grosser", x);
		stopwords.put("gro�er", x);
		stopwords.put("grosses", x);
		stopwords.put("gro�es", x);
		stopwords.put("gr��e", x);
		stopwords.put("gr��en", x);
		stopwords.put("gut", x);
		stopwords.put("gute", x);
		stopwords.put("guter", x);
		stopwords.put("gutes", x);
		
		stopwords.put("h", x);
		stopwords.put("habe", x);
		stopwords.put("haben", x);
		stopwords.put("habt", x);
		stopwords.put("hallo", x);
		stopwords.put("hast", x);
		stopwords.put("hat", x);
		stopwords.put("hatte", x);
		stopwords.put("h�tte", x);
		stopwords.put("hatten", x);
		stopwords.put("h�tten", x);
		stopwords.put("heisst", x);
		stopwords.put("her", x);
		stopwords.put("herr", x);
		stopwords.put("heute", x);
		stopwords.put("hier", x);
		stopwords.put("hin", x);
		stopwords.put("hinter", x);
		stopwords.put("hoch", x);
		
		stopwords.put("i", x);
		stopwords.put("ich", x);
		stopwords.put("ihm", x);
		stopwords.put("ihn", x);
		stopwords.put("ihnen", x);
		stopwords.put("ihr", x);
		stopwords.put("ihre", x);
		stopwords.put("ihrem", x);
		stopwords.put("ihren", x);
		stopwords.put("ihrer", x);
		stopwords.put("ihres", x);
		stopwords.put("im", x);
		stopwords.put("im", x);
		stopwords.put("immer", x);
		stopwords.put("in", x);
		stopwords.put("in", x);
		stopwords.put("indem", x);
		stopwords.put("infolgedessen", x);
		stopwords.put("ins", x);
		stopwords.put("irgend", x);
		stopwords.put("ist", x);
		
		stopwords.put("j", x);
		stopwords.put("ja", x);
		stopwords.put("ja", x);
		stopwords.put("jahr", x);
		stopwords.put("jahre", x);
		stopwords.put("jahren", x);
		stopwords.put("je", x);
		stopwords.put("jede", x);
		stopwords.put("jedem", x);
		stopwords.put("jeden", x);
		stopwords.put("jeder", x);
		stopwords.put("jedermann", x);
		stopwords.put("jedermanns", x);
		stopwords.put("jedoch", x);
		stopwords.put("jemand", x);
		stopwords.put("jemandem", x);
		stopwords.put("jemanden", x);
		stopwords.put("jene", x);
		stopwords.put("jenem", x);
		stopwords.put("jenen", x);
		stopwords.put("jener", x);
		stopwords.put("jenes", x);
		stopwords.put("jetzt", x);
		
		stopwords.put("k", x);
		stopwords.put("kam", x);
		stopwords.put("kann", x);
		stopwords.put("kannst", x);
		stopwords.put("kaum", x);
		stopwords.put("kein", x);
		stopwords.put("keine", x);
		stopwords.put("keinem", x);
		stopwords.put("keinen", x);
		stopwords.put("keiner", x);
		stopwords.put("kleine", x);
		stopwords.put("kleinen", x);
		stopwords.put("kleiner", x);
		stopwords.put("kleines", x);
		stopwords.put("kommen", x);
		stopwords.put("kommt", x);
		stopwords.put("k�nnen", x);
		stopwords.put("k�nnt", x);
		stopwords.put("konnte", x);
		stopwords.put("k�nnte", x);
		stopwords.put("konnten", x);
		stopwords.put("kurz", x);
		
		stopwords.put("l", x);
		stopwords.put("lang", x);
		stopwords.put("lange", x);
		stopwords.put("lange", x);
		stopwords.put("leicht", x);
		stopwords.put("leide", x);
		stopwords.put("lieber", x);
		stopwords.put("los", x);
		
		stopwords.put("m", x);
		stopwords.put("machen", x);
		stopwords.put("macht", x);
		stopwords.put("machte", x);
		stopwords.put("mag", x);
		stopwords.put("magst", x);
		stopwords.put("mahn", x);
		stopwords.put("man", x);
		stopwords.put("manche", x);
		stopwords.put("manchem", x);
		stopwords.put("manchen", x);
		stopwords.put("mancher", x);
		stopwords.put("manches", x);
		stopwords.put("mann", x);
		stopwords.put("mehr", x);
		stopwords.put("mein", x);
		stopwords.put("meine", x);
		stopwords.put("meinem", x);
		stopwords.put("meinen", x);
		stopwords.put("meiner", x);
		stopwords.put("meines", x);
		stopwords.put("mensch", x);
		stopwords.put("menschen", x);
		stopwords.put("mich", x);
		stopwords.put("mir", x);
		stopwords.put("mit", x);
		stopwords.put("mittel", x);
		stopwords.put("mochte", x);
		stopwords.put("m�chte", x);
		stopwords.put("mochten", x);
		stopwords.put("m�gen", x);
		stopwords.put("m�glich", x);
		stopwords.put("m�gt", x);
		stopwords.put("morgen", x);
		stopwords.put("muss", x);
		stopwords.put("mu�", x);
		stopwords.put("m�ssen", x);
		stopwords.put("musst", x);
		stopwords.put("m�sst", x);
		stopwords.put("musste", x);
		stopwords.put("mussten", x);
		
		stopwords.put("n", x);
		stopwords.put("na", x);
		stopwords.put("nach", x);
		stopwords.put("nachdem", x);
		stopwords.put("nahm", x);
		stopwords.put("nat�rlich", x);
		stopwords.put("neben", x);
		stopwords.put("nein", x);
		stopwords.put("neue", x);
		stopwords.put("neuen", x);
		stopwords.put("neun", x);
		stopwords.put("neunte", x);
		stopwords.put("neunten", x);
		stopwords.put("neunter", x);
		stopwords.put("neuntes", x);
		stopwords.put("nicht", x);
		stopwords.put("nicht", x);
		stopwords.put("nichts", x);
		stopwords.put("nie", x);
		stopwords.put("niemand", x);
		stopwords.put("niemandem", x);
		stopwords.put("niemanden", x);
		stopwords.put("noch", x);
		stopwords.put("nun", x);
		stopwords.put("nun", x);
		stopwords.put("nur", x);
		
		stopwords.put("o", x);
		stopwords.put("ob", x);
		stopwords.put("ob", x);
		stopwords.put("oben", x);
		stopwords.put("oder", x);
		stopwords.put("oder", x);
		stopwords.put("offen", x);
		stopwords.put("oft", x);
		stopwords.put("oft", x);
		stopwords.put("ohne", x);
		stopwords.put("Ordnung", x);
		
		stopwords.put("p", x);
		
		stopwords.put("q", x);
		
		stopwords.put("r", x);
		stopwords.put("recht", x);
		stopwords.put("rechte", x);
		stopwords.put("rechten", x);
		stopwords.put("rechter", x);
		stopwords.put("rechtes", x);
		stopwords.put("richtig", x);
		stopwords.put("rund", x);
		
		stopwords.put("s", x);
		stopwords.put("sa", x);
		stopwords.put("sache", x);
		stopwords.put("sagt", x);
		stopwords.put("sagte", x);
		stopwords.put("sah", x);
		stopwords.put("satt", x);
		stopwords.put("schlecht", x);
		stopwords.put("Schluss", x);
		stopwords.put("schon", x);
		stopwords.put("sechs", x);
		stopwords.put("sechste", x);
		stopwords.put("sechsten", x);
		stopwords.put("sechster", x);
		stopwords.put("sechstes", x);
		stopwords.put("sehr", x);
		stopwords.put("sei", x);
		stopwords.put("sei", x);
		stopwords.put("seid", x);
		stopwords.put("seien", x);
		stopwords.put("sein", x);
		stopwords.put("seine", x);
		stopwords.put("seinem", x);
		stopwords.put("seinen", x);
		stopwords.put("seiner", x);
		stopwords.put("seines", x);
		stopwords.put("seit", x);
		stopwords.put("seitdem", x);
		stopwords.put("selbst", x);
		stopwords.put("selbst", x);
		stopwords.put("sich", x);
		stopwords.put("sie", x);
		stopwords.put("sieben", x);
		stopwords.put("siebente", x);
		stopwords.put("siebenten", x);
		stopwords.put("siebenter", x);
		stopwords.put("siebentes", x);
		stopwords.put("sind", x);
		stopwords.put("so", x);
		stopwords.put("solang", x);
		stopwords.put("solche", x);
		stopwords.put("solchem", x);
		stopwords.put("solchen", x);
		stopwords.put("solcher", x);
		stopwords.put("solches", x);
		stopwords.put("soll", x);
		stopwords.put("sollen", x);
		stopwords.put("sollte", x);
		stopwords.put("sollten", x);
		stopwords.put("sondern", x);
		stopwords.put("sonst", x);
		stopwords.put("sowie", x);
		stopwords.put("sp�ter", x);
		stopwords.put("statt", x);
		
		stopwords.put("t", x);
		stopwords.put("tag", x);
		stopwords.put("tage", x);
		stopwords.put("tagen", x);
		stopwords.put("tat", x);
		stopwords.put("teil", x);
		stopwords.put("tel", x);
		stopwords.put("tritt", x);
		stopwords.put("trotzdem", x);
		stopwords.put("tun", x);
		
		stopwords.put("u", x);
		stopwords.put("�ber", x);
		stopwords.put("�berhaupt", x);
		stopwords.put("�brigens", x);
		stopwords.put("uhr", x);
		stopwords.put("um", x);
		stopwords.put("und", x);
		stopwords.put("und?", x);
		stopwords.put("uns", x);
		stopwords.put("unser", x);
		stopwords.put("unsere", x);
		stopwords.put("unserer", x);
		stopwords.put("unter", x);
		
		stopwords.put("v", x);
		stopwords.put("vergangenen", x);
		stopwords.put("viel", x);
		stopwords.put("viele", x);
		stopwords.put("vielem", x);
		stopwords.put("vielen", x);
		stopwords.put("vielleicht", x);
		stopwords.put("vier", x);
		stopwords.put("vierte", x);
		stopwords.put("vierten", x);
		stopwords.put("vierter", x);
		stopwords.put("viertes", x);
		stopwords.put("vom", x);
		stopwords.put("von", x);
		stopwords.put("vor", x);
		
		stopwords.put("w", x);
		stopwords.put("wahr?", x);
		stopwords.put("w�hrend", x);
		stopwords.put("w�hrenddem", x);
		stopwords.put("w�hrenddessen", x);
		stopwords.put("wann", x);
		stopwords.put("war", x);
		stopwords.put("w�re", x);
		stopwords.put("waren", x);
		stopwords.put("wart", x);
		stopwords.put("warum", x);
		stopwords.put("was", x);
		stopwords.put("wegen", x);
		stopwords.put("weil", x);
		stopwords.put("weit", x);
		stopwords.put("weiter", x);
		stopwords.put("weitere", x);
		stopwords.put("weiteren", x);
		stopwords.put("weiteres", x);
		stopwords.put("welche", x);
		stopwords.put("welchem", x);
		stopwords.put("welchen", x);
		stopwords.put("welcher", x);
		stopwords.put("welches", x);
		stopwords.put("wem", x);
		stopwords.put("wen", x);
		stopwords.put("wenig", x);
		stopwords.put("wenig", x);
		stopwords.put("wenige", x);
		stopwords.put("weniger", x);
		stopwords.put("weniges", x);
		stopwords.put("wenigstens", x);
		stopwords.put("wenn", x);
		stopwords.put("wenn", x);
		stopwords.put("wer", x);
		stopwords.put("werde", x);
		stopwords.put("werden", x);
		stopwords.put("werdet", x);
		stopwords.put("wessen", x);
		stopwords.put("wie", x);
		stopwords.put("wie", x);
		stopwords.put("wieder", x);
		stopwords.put("will", x);
		stopwords.put("willst", x);
		stopwords.put("wir", x);
		stopwords.put("wird", x);
		stopwords.put("wirklich", x);
		stopwords.put("wirst", x);
		stopwords.put("wo", x);
		stopwords.put("wohl", x);
		stopwords.put("wollen", x);
		stopwords.put("wollt", x);
		stopwords.put("wollte", x);
		stopwords.put("wollten", x);
		stopwords.put("worden", x);
		stopwords.put("wurde", x);
		stopwords.put("w�rde", x);
		stopwords.put("wurden", x);
		stopwords.put("w�rden", x);
		
		stopwords.put("x", x);
		
		stopwords.put("y", x);
		
		stopwords.put("z", x);
		stopwords.put("z.b", x);
		stopwords.put("zehn", x);
		stopwords.put("zehnte", x);
		stopwords.put("zehnten", x);
		stopwords.put("zehnter", x);
		stopwords.put("zehntes", x);
		stopwords.put("zeit", x);
		stopwords.put("zu", x);
		stopwords.put("zuerst", x);
		stopwords.put("zugleich", x);
		stopwords.put("zum", x);
		stopwords.put("zum", x);
		stopwords.put("zun�chst", x);
		stopwords.put("zur", x);
		stopwords.put("zur�ck", x);
		stopwords.put("zusammen", x);
		stopwords.put("zwanzig", x);
		stopwords.put("zwar", x);
		stopwords.put("zwar", x);
		stopwords.put("zwei", x);
		stopwords.put("zweite", x);
		stopwords.put("zweiten", x);
		stopwords.put("zweiter", x);
		stopwords.put("zweites", x);
		stopwords.put("zwischen", x);
		stopwords.put("zw�lf", x);
	}
}