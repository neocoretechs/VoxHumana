package com.neocoretechs.talker;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

public final class VoxHumana {
	  private SynthesizerModeDesc desc;
	  private Synthesizer synthesizer;
	  private Voice voice;
	  private static VoxHumana instance = null;
	  public static VoxHumana getInstance() {
		  if( instance == null ) {
			  try {
				instance = new VoxHumana();
			} catch (EngineException | AudioException | EngineStateError| PropertyVetoException e) {
				e.printStackTrace();
			}
		  }
		  return instance;
	  }
	private VoxHumana() throws EngineException, AudioException, EngineStateError, PropertyVetoException { 
	    // high quality
		init("kevin16");
	}
		  
	public void init(String voiceName)   throws EngineException, AudioException, EngineStateError,  PropertyVetoException  {
		    if (desc == null) {     
		    	System.setProperty("freetts.voices", 
		    	        "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		      
		      desc = new SynthesizerModeDesc(Locale.US);
		      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
		      synthesizer = Central.createSynthesizer(desc);
		      synthesizer.allocate();
		      synthesizer.resume();
		      SynthesizerModeDesc smd = (SynthesizerModeDesc)synthesizer.getEngineModeDesc();
		      Voice[] voices = smd.getVoices();
		      //for(Voice v: voices) System.out.println("Voice:"+v);
		      voice = null;
		      for(int i = 0; i < voices.length; i++) {
		        if(voices[i].getName().equals(voiceName)) {
		          voice = voices[i];
		          break;
		        }
		      }
		      synthesizer.getSynthesizerProperties().setVoice(voice);
		    }
		    
	}

	public void terminate() throws EngineException, EngineStateError {
		    synthesizer.deallocate();
	}
		  
	public void doSpeak(String speakText)   throws EngineException, AudioException, IllegalArgumentException,  InterruptedException  {
		      synthesizer.speakPlainText(speakText, null);
		      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

	}
	
    public static String[] readAllLines(String filePath, String fileName, String commentLineDelim) throws IOException {
    	FileReader fr = new FileReader(filePath + fileName);
        BufferedReader bufferedReader = new BufferedReader(fr);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        	System.out.println(line);
            if( !line.startsWith(commentLineDelim)) {
            	lines.add(line);
            }
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
    
	public static void main (String[]args) throws Exception {
		    VoxHumana su = new VoxHumana();
		    String[] speech = readAllLines(args[0], args[1], ";");
		    for(String s : speech)
		    	su.doSpeak(s);
		    su.terminate();
	}

}
