// Generated from vq.g4 by ANTLR 4.6
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class vqLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, BOOL=20, NEG=21, ID=22, NAT=23, FLOAT=24, WS=25;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "BOOL", "NEG", "ID", "NAT", "FLOAT", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "','", "']'", "':'", "'('", "')'", "'!'", "'*'", "'/'", "'+'", 
		"'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&&'", "'||'", "'.'", null, 
		"'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, "BOOL", "NEG", "ID", "NAT", 
		"FLOAT", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public vqLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "vq.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\33\u008e\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3"+
		"\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25k\n\25\3\26\3\26"+
		"\3\27\3\27\7\27q\n\27\f\27\16\27t\13\27\3\30\6\30w\n\30\r\30\16\30x\3"+
		"\31\6\31|\n\31\r\31\16\31}\3\31\3\31\6\31\u0082\n\31\r\31\16\31\u0083"+
		"\5\31\u0086\n\31\3\32\6\32\u0089\n\32\r\32\16\32\u008a\3\32\3\32\2\2\33"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\3\2\6\5\2C\\aac|\6"+
		"\2\62;C\\aac|\3\2\62;\5\2\13\f\17\17\"\"\u0094\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2"+
		"\2\2\3\65\3\2\2\2\5\67\3\2\2\2\79\3\2\2\2\t;\3\2\2\2\13=\3\2\2\2\r?\3"+
		"\2\2\2\17A\3\2\2\2\21C\3\2\2\2\23E\3\2\2\2\25G\3\2\2\2\27I\3\2\2\2\31"+
		"K\3\2\2\2\33N\3\2\2\2\35P\3\2\2\2\37S\3\2\2\2!V\3\2\2\2#Y\3\2\2\2%\\\3"+
		"\2\2\2\'_\3\2\2\2)j\3\2\2\2+l\3\2\2\2-n\3\2\2\2/v\3\2\2\2\61{\3\2\2\2"+
		"\63\u0088\3\2\2\2\65\66\7]\2\2\66\4\3\2\2\2\678\7.\2\28\6\3\2\2\29:\7"+
		"_\2\2:\b\3\2\2\2;<\7<\2\2<\n\3\2\2\2=>\7*\2\2>\f\3\2\2\2?@\7+\2\2@\16"+
		"\3\2\2\2AB\7#\2\2B\20\3\2\2\2CD\7,\2\2D\22\3\2\2\2EF\7\61\2\2F\24\3\2"+
		"\2\2GH\7-\2\2H\26\3\2\2\2IJ\7>\2\2J\30\3\2\2\2KL\7>\2\2LM\7?\2\2M\32\3"+
		"\2\2\2NO\7@\2\2O\34\3\2\2\2PQ\7@\2\2QR\7?\2\2R\36\3\2\2\2ST\7?\2\2TU\7"+
		"?\2\2U \3\2\2\2VW\7#\2\2WX\7?\2\2X\"\3\2\2\2YZ\7(\2\2Z[\7(\2\2[$\3\2\2"+
		"\2\\]\7~\2\2]^\7~\2\2^&\3\2\2\2_`\7\60\2\2`(\3\2\2\2ab\7v\2\2bc\7t\2\2"+
		"cd\7w\2\2dk\7g\2\2ef\7h\2\2fg\7c\2\2gh\7n\2\2hi\7u\2\2ik\7g\2\2ja\3\2"+
		"\2\2je\3\2\2\2k*\3\2\2\2lm\7/\2\2m,\3\2\2\2nr\t\2\2\2oq\t\3\2\2po\3\2"+
		"\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2s.\3\2\2\2tr\3\2\2\2uw\t\4\2\2vu\3\2"+
		"\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2\2\2y\60\3\2\2\2z|\t\4\2\2{z\3\2\2\2|}\3"+
		"\2\2\2}{\3\2\2\2}~\3\2\2\2~\u0085\3\2\2\2\177\u0081\7\60\2\2\u0080\u0082"+
		"\t\4\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085\177\3\2\2\2\u0085\u0086\3\2\2"+
		"\2\u0086\62\3\2\2\2\u0087\u0089\t\5\2\2\u0088\u0087\3\2\2\2\u0089\u008a"+
		"\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\3\2\2\2\u008c"+
		"\u008d\b\32\2\2\u008d\64\3\2\2\2\n\2jrx}\u0083\u0085\u008a\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}