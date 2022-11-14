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
	private LiveSpeechRecognizer recognizer; // reconhece o que est� sendo dito
	private String speechRecognitionResult; // joga o que est� sendo dito para texto
	private boolean ignoreSpeechRecognitionResults = false; // ignora o que � dito caso n�o consiga parar
	private boolean speechRecognizerThreadRunning = false; // verifica se o reconhecimento j� est� rodando
	private boolean resourcesThreadRunning; // verifica se a thread de recursos j� est� rodando
	private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2); // usado para executar eventos em
																						// ordem (tipo thread)

	// construtor
	public MainTeste() {

		logger.log(Level.INFO, "Loading Speech Recognizer...\n"); // joga esse texto pro console
		Configuration configuration = new Configuration(); // inicia a vari�vel com as configura��es
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us"); // seta o caminho do modelo acustico
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict"); // seta o dicion�rio

		// * Se descomentar a linha abaixo ele reconhecer� todas as palavras da l�ngua, n�o necessitando do grammar *
		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		// Se n�o for usar grammar se sim todas as palavras, comentar as 3 linhas
		// abaixo, al�m de liberar a de cima
		configuration.setGrammarPath("resource:/grammars"); // seta o caminho onde h� o grammar
		configuration.setGrammarName("grammar"); // seta o nome do grammar (acho que deve ser o nome do arquivo)
		configuration.setUseGrammar(true); // mostra se vai usar ou n�o o grammar

		try {
			recognizer = new LiveSpeechRecognizer(configuration); // aplica as configura��es ao recognizer
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex); // caso d� erro escreve no console
		}

		// recognizer.startRecognition(true); //inicia o recognizer removendo os dados
		// pr�vios em cache

		startResourcesThread(); // inicia a thread que verifica se os recursos est�o dispon�veis
		startSpeechRecognition(); // inicia a thread que ir� reconhecer o que � falado
	}
	

	public void startSpeechRecognition() {
		if (speechRecognizerThreadRunning) //verifica se a thread de reconhecimento j� est� rodando
			logger.log(Level.INFO, "Speech Recognition Thread already running...\n");
		else
			eventsExecutorService.submit(() -> { //inicia o executor de servi�o // thread like
				
				speechRecognizerThreadRunning = true; //seta que a thread de reconhecimento come�ou a rodar
				ignoreSpeechRecognitionResults = false; //seta que n�o � necess�rio ignorar o reconhecimento
				
				recognizer.startRecognition(true); //recognizer come�a a trabalhar
				
				logger.log(Level.INFO, "You can start to speak...\n"); //mostra que o usu�rio pode come�ar a falar
				
				try {
					while (speechRecognizerThreadRunning) { //enquanto o recognizer estiver rodando/pessoa estiver falando...
						SpeechResult speechResult = recognizer.getResult(); //pega o que a pessoa fala e joga na vari�vel
						
						if (!ignoreSpeechRecognitionResults) { //roda se n�o estiver ignorando
							if (speechResult == null) //se o resultado for vazio/n�o for reconhecido
								logger.log(Level.INFO, "I can't understand what you said.\n"); //escreve que n�o entendeu o que foi dito
							else {
								speechRecognitionResult = speechResult.getHypothesis(); //pega a hip�tese do resultado da fala
								System.out.println("You said: [" + speechRecognitionResult + "]\n"); //mostra o que foi dito (?)
								
								//makeDecision(speechRecognitionResult, speechResult.getWords()); //aplicaria um m�todo sobre as palavras
							}
						} else {
							logger.log(Level.INFO, "Ingoring Speech Recognition Results..."); //mostra que est� ignorando
						}
					}
				} catch(Exception e) {
					logger.log(Level.WARNING, null, e); //exibe o erro
					speechRecognizerThreadRunning = false; //n�o est� mais rodando o reconhecimento
				}
				
				logger.log(Level.INFO, "SpeechThread has exited..."); //exibe que parou
				
			});
			
	}
	
	public synchronized void stopIgnoreSpeechRecognitionResults() {
		ignoreSpeechRecognitionResults = false; //para de ignorar o resultado a fala
	}
	
	public synchronized void ignoreSpeechRecognitionResults() {		
		ignoreSpeechRecognitionResults = true; //come�a a ignorar o resultado sem parar o processo
	}
	
	//thread que verifica a disponibilidade dos recursos
	public synchronized void startResourcesThread() {
		
		if (resourcesThreadRunning) //verifica se essa thread j� est� rodando
			logger.log(Level.INFO, "Resources Thread already running...\n");
		else
			eventsExecutorService.submit(() -> { //inicia o executor de servi�o
				
				try {
					resourcesThreadRunning = true; //seta que essa thread j� est� rodando
					
					while(true) {
						if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE)) //se o microfone n�o estiver dispon�vel
							logger.log(Level.INFO, "Microphone is not available.\n"); //exibe a indisponibilidade
						
						Thread.sleep(350); //d� uma pausa estrat�gica xD
					}
					
				} catch (InterruptedException e) { //pega erros relacionados ao sleep da thread
					logger.log(Level.WARNING, null, e);
					resourcesThreadRunning = false; //seta que a threado parou
				}
				
			});
	}
	
	public boolean getIgnoreSpeechRecognitionResults() {
		return ignoreSpeechRecognitionResults; //retorna se est� ignorando ou n�o
	}
	
	public boolean getSpeechRecognizerThreadRunning() {
		return speechRecognizerThreadRunning; //retorna se o reconhecimento est� rodando ou n�o
	}
	
	// inicia a classe no main
	public static void main(String[] args) {
		new MainTeste();
	}
}
