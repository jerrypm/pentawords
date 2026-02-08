package com.jeripurnama.pentaword.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryRepository(private val context: Context) {

    private var dictionary: Set<String>? = null

    suspend fun loadDictionary(): Set<String> = withContext(Dispatchers.IO) {
        dictionary?.let { return@withContext it }

        val words = mutableSetOf<String>()
        try {
            context.assets.open("words.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val word = line.trim().uppercase()
                    if (word.length >= 4 && word.all { it.isLetter() }) {
                        words.add(word)
                    }
                }
            }
        } catch (e: Exception) {
            words.addAll(getDefaultDictionary())
        }

        dictionary = words
        words
    }

    private fun getDefaultDictionary(): Set<String> {
        return setOf(
            "ABOUT", "ABOVE", "ADOPT", "AGENT", "AGREE", "AHEAD", "ALIEN", "ALLOW", "ALONE", "ALONG",
            "ALTER", "AMONG", "ANGER", "ANGLE", "ANGRY", "APART", "APPLE", "APPLY", "ARENA", "ARGUE",
            "ARISE", "ARMED", "ARMOR", "ARRAY", "ARROW", "ASIDE", "ASSET", "AVOID", "AWAKE", "AWARD",
            "AWARE", "BAKER", "BASED", "BASIC", "BEACH", "BEAST", "BEGAN", "BEGIN", "BEING", "BELOW",
            "BENCH", "BIRTH", "BLACK", "BLADE", "BLAME", "BLANK", "BLAST", "BLEND", "BLIND", "BLOCK",
            "BLOOD", "BLOOM", "BLOWN", "BLUES", "BOARD", "BOOST", "BOOTH", "BOUND", "BRAIN", "BRAND",
            "BRAVE", "BREAD", "BREAK", "BREED", "BRICK", "BRIDE", "BRIEF", "BRING", "BROAD", "BROKE",
            "BROWN", "BUILD", "BUILT", "BUNCH", "BUYER", "CABLE", "CALIF", "CAMPS", "CANDY", "CARDS",
            "CARGO", "CARRY", "CASES", "CATCH", "CAUSE", "CEASE", "CHAIN", "CHAIR", "CHAOS", "CHARM",
            "CHART", "CHASE", "CHEAP", "CHECK", "CHEST", "CHIEF", "CHILD", "CHINA", "CHOSE", "CHUNK",
            "CLAIM", "CLASS", "CLEAN", "CLEAR", "CLERK", "CLICK", "CLIMB", "CLOCK", "CLOSE", "CLOTH",
            "CLOUD", "COACH", "COAST", "COULD", "COUNT", "COURT", "COVER", "CRAFT", "CRASH", "CRAZY",
            "CREAM", "CRIME", "CROSS", "CROWD", "CROWN", "CRUEL", "CRUSH", "CYCLE", "DAILY", "DANCE",
            "DATED", "DEALT", "DEATH", "DEBUT", "DELAY", "DELTA", "DENSE", "DEPTH", "DIRTY", "DOUBT",
            "DOZEN", "DRAFT", "DRAIN", "DRAMA", "DRANK", "DRAWN", "DREAM", "DRESS", "DRIED", "DRINK",
            "DRIVE", "DROVE", "DROWN", "DRUGS", "DRUNK", "DYING", "EAGER", "EARLY", "EARTH", "EATEN",
            "EDGES", "EIGHT", "ELECT", "ELITE", "EMPTY", "ENEMY", "ENJOY", "ENTER", "ENTRY", "EQUAL",
            "ERROR", "ESSAY", "EVENT", "EVERY", "EXACT", "EXIST", "EXTRA", "FACED", "FACTS", "FAINT",
            "FAITH", "FALSE", "FANCY", "FATAL", "FAULT", "FAVOR", "FEAST", "FIBER", "FIELD", "FIFTH",
            "FIFTY", "FIGHT", "FINAL", "FINDS", "FIRED", "FIRST", "FIXED", "FLAME", "FLASH", "FLEET",
            "FLESH", "FLOAT", "FLOOD", "FLOOR", "FLOUR", "FLOWS", "FLUID", "FLUSH", "FOCUS", "FORCE",
            "FORTH", "FORTY", "FORUM", "FOUND", "FRAME", "FRANK", "FRAUD", "FRESH", "FRONT", "FROST",
            "FRUIT", "FULLY", "FUNDS", "GAMES", "GENES", "GHOST", "GIANT", "GIVEN", "GIVES", "GLASS",
            "GLOBE", "GLORY", "GOING", "GOODS", "GRACE", "GRADE", "GRAIN", "GRAND", "GRANT", "GRAPE",
            "GRASP", "GRASS", "GRAVE", "GREAT", "GREEN", "GREET", "GRIEF", "GRILL", "GRIND", "GROSS",
            "GROUP", "GROVE", "GROWN", "GUARD", "GUESS", "GUEST", "GUIDE", "GUILT", "HABIT", "HANDS",
            "HAPPY", "HARSH", "HASTE", "HAVEN", "HEADS", "HEARD", "HEART", "HEAVY", "HEDGE", "HEELS",
            "HELLO", "HENCE", "HERBS", "HILLS", "HOLDS", "HOLES", "HOMES", "HONEY", "HONOR", "HOPED",
            "HOPES", "HORSE", "HOSTS", "HOTEL", "HOURS", "HOUSE", "HUMAN", "HUMOR", "IDEAL", "IDEAS",
            "IMAGE", "IMPLY", "INDEX", "INDIA", "INNER", "INPUT", "ISSUE", "ITEMS", "JAPAN", "JEANS",
            "JOINT", "JONES", "JUDGE", "JUICE", "KEEPS", "KINDS", "KNIFE", "KNOCK", "KNOWN", "KNOWS",
            "LABEL", "LABOR", "LAKES", "LANCE", "LANDS", "LANES", "LARGE", "LASER", "LATER", "LAUGH",
            "LAYER", "LEADS", "LEARN", "LEASE", "LEAST", "LEAVE", "LEGAL", "LEMON", "LEVEL", "LEWIS",
            "LIGHT", "LIKED", "LIKES", "LIMIT", "LINES", "LINKS", "LISTS", "LIVED", "LIVER", "LIVES",
            "LOBBY", "LOCAL", "LOGIC", "LOOKS", "LOOSE", "LORDS", "LOSES", "LOVED", "LOVER", "LOWER",
            "LUCKY", "LUNCH", "LYING", "MAGIC", "MAJOR", "MAKER", "MALES", "MANOR", "MARCH", "MARKS",
            "MARSH", "MATCH", "MAYOR", "MEALS", "MEANS", "MEANT", "MEDAL", "MEDIA", "MELON", "MERCY",
            "MERGE", "MERIT", "MERRY", "METAL", "MICRO", "MIDST", "MIGHT", "MILES", "MILLS", "MINDS",
            "MINES", "MINOR", "MINUS", "MIXED", "MODEL", "MODES", "MONEY", "MONTH", "MORAL", "MOTOR",
            "MOUNT", "MOUSE", "MOUTH", "MOVED", "MOVES", "MOVIE", "MUSIC", "NAMES", "NAVAL", "NEEDS",
            "NERVE", "NEVER", "NEWLY", "NIGHT", "NINTH", "NOBLE", "NODES", "NOISE", "NORTH", "NOTED",
            "NOTES", "NOVEL", "NURSE", "OCCUR", "OCEAN", "OFFER", "OFTEN", "OLIVE", "ONSET", "OPENS",
            "OPERA", "ORBIT", "ORDER", "OTHER", "OUGHT", "OUTER", "OWNED", "OWNER", "OXIDE", "OZONE",
            "PAGES", "PAINT", "PAIRS", "PANEL", "PANIC", "PANTS", "PAPER", "PARKS", "PARTS", "PARTY",
            "PASTA", "PASTE", "PATCH", "PATHS", "PAUSE", "PEACE", "PEARL", "PENNY", "PHASE", "PHONE",
            "PHOTO", "PIANO", "PICKS", "PIECE", "PILOT", "PINCH", "PIPES", "PITCH", "PIZZA", "PLACE",
            "PLAIN", "PLANE", "PLANS", "PLANT", "PLATE", "PLAYS", "PLAZA", "PLEAD", "PLOTS", "POINT",
            "POLAR", "POLLS", "POOLS", "PORCH", "PORTS", "POSED", "POSES", "POSTS", "POUND", "POWER",
            "PRESS", "PRICE", "PRIDE", "PRIME", "PRINT", "PRIOR", "PRIZE", "PROBE", "PROOF", "PROUD",
            "PROVE", "PROXY", "PUNCH", "PUPIL", "QUEEN", "QUEST", "QUICK", "QUIET", "QUITE", "QUOTE",
            "RACES", "RADAR", "RADIO", "RAISE", "RALLY", "RANCH", "RANGE", "RANKS", "RAPID", "RATES",
            "RATIO", "REACH", "REACT", "READS", "READY", "REALM", "REBEL", "REFER", "REIGN", "RELAX",
            "REPLY", "RESET", "RIDGE", "RIFLE", "RIGHT", "RIGID", "RINGS", "RISES", "RISKS", "RISKY",
            "RIVAL", "RIVER", "ROADS", "ROBIN", "ROBOT", "ROCKS", "ROCKY", "ROLES", "ROMAN", "ROOMS",
            "ROOTS", "ROUGH", "ROUND", "ROUTE", "ROYAL", "RUGBY", "RULED", "RULER", "RULES", "RURAL",
            "SAFER", "SAINT", "SALAD", "SALES", "SALON", "SANDY", "SANTA", "SAUCE", "SAVED", "SAVES",
            "SCALE", "SCENE", "SCOPE", "SCORE", "SCOUT", "SCRAP", "SEATS", "SEEMS", "SEIZE", "SELLS",
            "SENDS", "SENSE", "SERVE", "SETUP", "SEVEN", "SHADE", "SHAKE", "SHALL", "SHAME", "SHAPE",
            "SHARE", "SHARK", "SHARP", "SHEEP", "SHEER", "SHEET", "SHELF", "SHELL", "SHIFT", "SHINE",
            "SHIPS", "SHIRT", "SHOCK", "SHOES", "SHOOT", "SHOPS", "SHORE", "SHORT", "SHOTS", "SHOUT",
            "SHOWN", "SHOWS", "SIDES", "SIGHT", "SIGMA", "SIGNS", "SILLY", "SIMON", "SINCE", "SITES",
            "SIXTH", "SIXTY", "SIZED", "SIZES", "SKILL", "SLEEP", "SLICE", "SLIDE", "SLOPE", "SLOTS",
            "SMALL", "SMART", "SMELL", "SMILE", "SMITH", "SMOKE", "SNAKE", "SOLAR", "SOLID", "SOLVE",
            "SORRY", "SORTS", "SOULS", "SOUND", "SOUTH", "SPACE", "SPARE", "SPARK", "SPEAK", "SPECS",
            "SPEED", "SPELL", "SPEND", "SPENT", "SPIKE", "SPINE", "SPLIT", "SPOKE", "SPORT", "SPOTS",
            "SPRAY", "SQUAD", "STACK", "STAFF", "STAGE", "STAKE", "STAMP", "STAND", "STARK", "STARS",
            "START", "STATE", "STAYS", "STEAK", "STEAL", "STEAM", "STEEL", "STEEP", "STEMS", "STEPS",
            "STICK", "STIFF", "STILL", "STOCK", "STONE", "STOOD", "STOPS", "STORE", "STORM", "STORY",
            "STRAP", "STRAW", "STRIP", "STUCK", "STUFF", "STYLE", "SUGAR", "SUITE", "SUITS", "SUNNY",
            "SUPER", "SURGE", "SWEET", "SWEPT", "SWIFT", "SWING", "SWISS", "SWORD", "SWUNG", "TABLE",
            "TAKEN", "TAKES", "TALES", "TALKS", "TANKS", "TAPES", "TASKS", "TASTE", "TAXES", "TEACH",
            "TEAMS", "TEARS", "TEENS", "TEETH", "TELLS", "TEMPO", "TENDS", "TENOR", "TENSE", "TENTH",
            "TERMS", "TESTS", "TEXAS", "TEXTS", "THANK", "THEFT", "THEIR", "THEME", "THERE", "THESE",
            "THICK", "THIEF", "THING", "THINK", "THIRD", "THOSE", "THREE", "THREW", "THROW", "THUMB",
            "TIGHT", "TIMER", "TIMES", "TINY", "TIRED", "TITLE", "TODAY", "TOKEN", "TOKYO", "TONES",
            "TOOLS", "TOOTH", "TOPIC", "TORCH", "TOTAL", "TOUCH", "TOUGH", "TOURS", "TOWER", "TOWNS",
            "TRACE", "TRACK", "TRACT", "TRADE", "TRAIL", "TRAIN", "TRAIT", "TRANS", "TRASH", "TREAT",
            "TREES", "TREND", "TRIAL", "TRIBE", "TRICK", "TRIED", "TRIES", "TRIPS", "TROOP", "TRUCK",
            "TRULY", "TRUNK", "TRUST", "TRUTH", "TUBES", "TUMOR", "TUNED", "TURNS", "TWICE", "TWIST",
            "TYPES", "UNCLE", "UNDER", "UNION", "UNITE", "UNITS", "UNITY", "UNTIL", "UPPER", "UPSET",
            "URBAN", "URGED", "USAGE", "USERS", "USING", "USUAL", "VALID", "VALUE", "VALVE", "VAPOR",
            "VAULT", "VEGAS", "VENUE", "VERBS", "VERSE", "VIDEO", "VIEWS", "VILLA", "VINYL", "VIRUS",
            "VISIT", "VITAL", "VOCAL", "VOICE", "VOTED", "VOTER", "VOTES", "WAGES", "WAGON", "WAIST",
            "WALLS", "WANTS", "WASTE", "WATCH", "WATER", "WAVES", "WEALTH", "WEEKS", "WEIGH", "WEIRD",
            "WELLS", "WELSH", "WHEAT", "WHEEL", "WHERE", "WHICH", "WHILE", "WHITE", "WHOLE", "WHOSE",
            "WIDER", "WIDTH", "WINDS", "WINES", "WINGS", "WIRED", "WIRES", "WITCH", "WIVES", "WOMAN",
            "WOMEN", "WOODS", "WORDS", "WORKS", "WORLD", "WORRY", "WORSE", "WORST", "WORTH", "WOULD",
            "WOUND", "WRIST", "WRITE", "WRONG", "WROTE", "YACHT", "YARDS", "YEARS", "YIELD", "YOUNG",
            "YOURS", "YOUTH", "ZONES", "PENTA", "WEPT", "PEAT", "NEAT", "TAPE", "ANTE", "PANT", "WANTON",
            "PATENT", "POTENT", "WEAPON", "PONENT"
        )
    }
}
