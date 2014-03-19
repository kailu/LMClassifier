package edu.berkeley.nlp.lm.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;

import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.berkeley.nlp.lm.collections.Iterators;
import edu.berkeley.nlp.lm.util.Logger;

/**
 * Computes the log probability of a list of files. With the <code>-g</code>
 * option, it interprets the next two arguments as a <code>vocab_cs.gz</code>
 * file (see {@link LmReaders} for more detail) and a Berkeley LM binary,
 * respectively. Without <code>-g</code>, it interprets the next file as a
 * Berkeley LM binary. All remaining files are treated as plain-text (possibly
 * gzipped) files which have one sentence per line; a dash is used to indicate
 * that text should from standard input. If no files are given, reads from
 * standard input.
 * 
 * @author kailu
 * 
 */
public class LMClassifier
{
	
	private HashMap<String,NgramLanguageModel<String> > allLMs = new HashMap<String,NgramLanguageModel<String> >(); 
	
	public static class CLASSIFY_ITEM implements Comparable<CLASSIFY_ITEM>{
		public String cat;
		public float value;
		@Override
		public int compareTo(CLASSIFY_ITEM rhs) {
			if(this.value == rhs.value) return 0;
			if((this.value-rhs.value) < 0) return 1;
			
			return -1;
		}
	}
	
	public static File[] getModelFiles(String dir){
		ArrayList<File> result = new ArrayList<File>();
		File dirFile = new File(dir);
		for(File f: dirFile.listFiles()){
			if(f.isFile() && !f.isHidden()){
				result.add(f);
			}
		}
		File[] fileArray = new File[result.size()];
		result.toArray(fileArray);
		return fileArray;
	}
	
	public void loadModels(String modelDir){
		File[] modelFiles = LMClassifier.getModelFiles(modelDir);
		for(File file : modelFiles){
			System.err.println("loaded model file:" + file.getAbsolutePath());
			this.allLMs.put(file.getName(), LMClassifier.readBinary(false, null, file.getAbsolutePath()));
		}
	}
	
	public List<LMClassifier.CLASSIFY_ITEM> classify(String input){
		List<String> words = Arrays.asList(input.trim().split("\\s+"));
		ArrayList<LMClassifier.CLASSIFY_ITEM> result = new ArrayList<LMClassifier.CLASSIFY_ITEM>();
		for(String dd : this.allLMs.keySet()){
			LMClassifier.CLASSIFY_ITEM item = new LMClassifier.CLASSIFY_ITEM();
			item.cat = dd;
			
			item.value = this.allLMs.get(dd).getLogProb(words);
			
			result.add(item);
		}
		
		//sort result based on the value
		Collections.sort(result);
		return result;
	}

	/**
	 * 
	 */
	private static void usage() {
		System.err.println("Usage: <Berkeley LM binary file> \n");
		System.exit(1);
	}
/*
 *  Accept the input to classify from standard input, print out output on standard output;
 *  Need one parameter that specify where the LMs are, Model file name as the category name in final output
 */
	public static void main(final String[] argv) throws FileNotFoundException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(System.in)));
		String line = null;
		LMClassifier classifier = new LMClassifier();
		String modelDir = argv[0];
		classifier.loadModels(modelDir);
		while((line = br.readLine()) != null){
			
			List<LMClassifier.CLASSIFY_ITEM> probs = classifier.classify(line);
			for(LMClassifier.CLASSIFY_ITEM item : probs){
				System.out.print(item.cat+":" + item.value + "\t");
			}
			System.out.println();
		}
	}



	/**
	 * @param isGoogleBinary
	 * @param vocabFile
	 * @param binaryFile
	 * @return
	 */
	private static NgramLanguageModel<String> readBinary(boolean isGoogleBinary, String vocabFile, String binaryFile) {
		NgramLanguageModel<String> lm = null;
		if (isGoogleBinary) {
			Logger.startTrack("Reading Google Binary " + binaryFile + " with vocab " + vocabFile);
			lm = LmReaders.readGoogleLmBinary(binaryFile, vocabFile);
			Logger.endTrack();
		} else {
			Logger.startTrack("Reading LM Binary " + binaryFile);
			lm = LmReaders.readLmBinary(binaryFile);
			Logger.endTrack();
		}
		return lm;
	}

}
