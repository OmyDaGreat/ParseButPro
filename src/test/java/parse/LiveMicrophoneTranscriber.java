package parse;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class LiveMicrophoneTranscriber {

    private LiveSpeechRecognizer recognizer;

    public LiveMicrophoneTranscriber() throws IOException {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        recognizer = new LiveSpeechRecognizer(configuration);
    }

    public void startRecognition() {
        recognizer.startRecognition(true); // true indicates continuous recognition
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
	        log.info("Hypothesis: {}", result.getHypothesis());
        }
        recognizer.stopRecognition(); // Stop recognition when done
    }

    public static void main(String[] args) throws IOException {
        LiveMicrophoneTranscriber transcriber = new LiveMicrophoneTranscriber();
        transcriber.startRecognition();
    }
}