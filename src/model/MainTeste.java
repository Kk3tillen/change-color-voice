package model;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class MainTeste {

	private Logger logger = Logger.getLogger(getClass().getName()); // o console
	private LiveSpeechRecognizer recognizer; // reconhece o que está sendo dito
	private String speechRecognitionResult; // joga o que está sendo dito para texto
	private boolean ignoreSpeechRecognitionResults = false; // ignora o que é dito caso não consiga parar
	private boolean speechRecognizerThreadRunning = false; // verifica se o reconhecimento já está rodando
	private boolean resourcesThreadRunning; // verifica se a thread de recursos já está rodando
	private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2); // usado para executar eventos em
																						// ordem (tipo thread)

	// construtor
	public MainTeste() {

		logger.log(Level.INFO, "Loading Speech Recognizer...\n"); // joga esse texto pro console
		Configuration configuration = new Configuration(); // inicia a variável com as configurações
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us"); // seta o caminho do modelo acustico
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict"); // seta o dicionário

		// * Se descomentar a linha abaixo ele reconhecerá todas as palavras da língua, não necessitando do grammar *
		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		// Se não for usar grammar se sim todas as palavras, comentar as 3 linhas
		// abaixo, além de liberar a de cima
		configuration.setGrammarPath("resource:/grammars"); // seta o caminho onde há o grammar
		configuration.setGrammarName("grammar"); // seta o nome do grammar (acho que deve ser o nome do arquivo)
		configuration.setUseGrammar(true); // mostra se vai usar ou não o grammar

		try {
			recognizer = new LiveSpeechRecognizer(configuration); // aplica as configurações ao recognizer
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex); // caso dê erro escreve no console
		}

		// recognizer.startRecognition(true); //inicia o recognizer removendo os dados
		// prévios em cache

		startResourcesThread(); // inicia a thread que verifica se os recursos estão disponíveis
		startSpeechRecognition(); // inicia a thread que irá reconhecer o que é falado
	}
	

	public void startSpeechRecognition() {
		if (speechRecognizerThreadRunning) //verifica se a thread de reconhecimento já está rodando
			logger.log(Level.INFO, "Speech Recognition Thread already running...\n");
		else
			eventsExecutorService.submit(() -> { //inicia o executor de serviço // thread like
				
				speechRecognizerThreadRunning = true; //seta que a thread de reconhecimento começou a rodar
				ignoreSpeechRecognitionResults = false; //seta que não é necessário ignorar o reconhecimento
				
				recognizer.startRecognition(true); //recognizer começa a trabalhar
				
				logger.log(Level.INFO, "You can start to speak...\n"); //mostra que o usuário pode começar a falar
				
				try {
					while (speechRecognizerThreadRunning) { //enquanto o recognizer estiver rodando/pessoa estiver falando...
						SpeechResult speechResult = recognizer.getResult(); //pega o que a pessoa fala e joga na variável
						
						if (!ignoreSpeechRecognitionResults) { //roda se não estiver ignorando
							if (speechResult == null) //se o resultado for vazio/não for reconhecido
								logger.log(Level.INFO, "I can't understand what you said.\n"); //escreve que não entendeu o que foi dito
							else {
								speechRecognitionResult = speechResult.getHypothesis(); //pega a hipótese do resultado da fala
								System.out.println("You said: [" + speechRecognitionResult + "]\n"); //mostra o que foi dito (?)
								
								//makeDecision(speechRecognitionResult, speechResult.getWords()); //aplicaria um método sobre as palavras
							}
						} else {
							logger.log(Level.INFO, "Ingoring Speech Recognition Results..."); //mostra que está ignorando
						}
					}
				} catch(Exception e) {
					logger.log(Level.WARNING, null, e); //exibe o erro
					speechRecognizerThreadRunning = false; //não está mais rodando o reconhecimento
				}
				
				logger.log(Level.INFO, "SpeechThread has exited..."); //exibe que parou
				
			});
			
	}
	
	public synchronized void stopIgnoreSpeechRecognitionResults() {
		ignoreSpeechRecognitionResults = false; //para de ignorar o resultado a fala
	}
	
	public synchronized void ignoreSpeechRecognitionResults() {		
		ignoreSpeechRecognitionResults = true; //começa a ignorar o resultado sem parar o processo
	}
	
	//thread que verifica a disponibilidade dos recursos
	public synchronized void startResourcesThread() {
		
		if (resourcesThreadRunning) //verifica se essa thread já está rodando
			logger.log(Level.INFO, "Resources Thread already running...\n");
		else
			eventsExecutorService.submit(() -> { //inicia o executor de serviço
				
				try {
					resourcesThreadRunning = true; //seta que essa thread já está rodando
					
					while(true) {
						if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE)) //se o microfone não estiver disponível
							logger.log(Level.INFO, "Microphone is not available.\n"); //exibe a indisponibilidade
						
						Thread.sleep(350); //dá uma pausa estratégica xD
					}
					
				} catch (InterruptedException e) { //pega erros relacionados ao sleep da thread
					logger.log(Level.WARNING, null, e);
					resourcesThreadRunning = false; //seta que a threado parou
				}
				
			});
	}
	
	public boolean getIgnoreSpeechRecognitionResults() {
		return ignoreSpeechRecognitionResults; //retorna se está ignorando ou não
	}
	
	public boolean getSpeechRecognizerThreadRunning() {
		return speechRecognizerThreadRunning; //retorna se o reconhecimento está rodando ou não
	}
	
	// inicia a classe no main
	public static void main(String[] args) {
		new MainTeste();
	}
}
