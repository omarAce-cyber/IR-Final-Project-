# Bilingual Information Retrieval Search Engine

## 1. Project Description
This project is an academic Information Retrieval (IR) system that supports **English** and **Arabic** text search. It demonstrates core IR workflow stages:
- document loading
- language-aware preprocessing
- positional indexing
- query parsing
- ranked retrieval
- evaluation

The system includes:
- bilingual preprocessing for Arabic and English
- **positional inverted index**
- **ranked retrieval with TF-IDF**
- **cosine similarity scoring**
- **spelling correction** (k-gram + edit distance)
- **phrase search**, **proximity search**, and **wildcard search**
- a minimal **Java Swing GUI**

---

## 2. Features
- English and Arabic text support
- Language-specific preprocessing
- Positional inverted index
- Phrase search
- Proximity search
- Wildcard search
- Spelling correction
- TF-IDF ranking
- Cosine similarity ranking
- Simple GUI using Java Swing
- Precision and Recall evaluation
- Execution-time reporting

---

## 3. Project Structure

```text
project_root/
├── documents/
│   ├── english/
│   └── arabic/
├── data/
│   ├── stopwords_en.txt
│   └── stopwords_ar.txt
├── src/
│   ├── preprocessing/
│   ├── indexing/
│   ├── query/
│   ├── ranking/
│   ├── evaluation/
│   ├── gui/
│   └── Main.java
├── results/
│   ├── evaluation_results.txt
│   └── sample_queries.txt
├── requirements.txt
└── README.md
```

### Folder Responsibilities
- `src/preprocessing`:
  - `EnglishProcessor.java`: English normalization/tokenization/stopword removal
  - `ArabicProcessor.java`: Arabic normalization/tokenization/stopword removal
  - `LanguageDetector.java`: detects Arabic vs English input

- `src/indexing`:
  - `PositionalInvertedIndex.java`: term → doc → positions
  - `KGramIndex.java`: k-gram index for wildcard expansion and spelling candidates

- `src/query`:
  - `QueryParser.java`: identifies term/phrase/proximity/wildcard query types
  - `SearchEngine.java`: query execution and ranked retrieval orchestration
  - `SpellingCorrector.java`: suggestion generation

- `src/ranking`:
  - `TFIDFCalculator.java`: TF-IDF vectorization and scoring
  - `CosineSimilarity.java`: similarity computation

- `src/evaluation`:
  - `Evaluator.java`: precision, recall, execution-time evaluation and file export

- `src/gui`:
  - `SearchGUI.java`: minimal Java Swing interface

- `results/`:
  - stores generated evaluation and sample query outputs

---

## 4. System Workflow
1. Load documents from `documents/english` and `documents/arabic`
2. Detect language and preprocess text
3. Build positional inverted index
4. Build k-gram index
5. Build TF-IDF vectors for ranked retrieval
6. Parse and execute queries
7. Rank candidate documents with cosine similarity
8. Show results in GUI and suggestions when available
9. Save evaluation and sample outputs to `results/`

---

## 5. Installation
### Prerequisites
- Java JDK 17 or newer

### Setup
1. Clone repository
2. Ensure JDK is installed:
   ```bash
   java -version
   javac -version
   ```

No external libraries are required.

---

## 6. How to Build and Run
### Command Line (Java)
From project root:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

### IDE (IntelliJ IDEA / Eclipse)
1. Open the project
2. Set SDK to JDK 17+
3. Run `Main.java`

---

## 7. GUI Design (Simple Swing)
The GUI uses only basic Swing components:
- `JFrame` (main window title: **Bilingual Search Engine**)
- `JTextField` (query input)
- `JButton` (search)
- `JTextArea` (results)
- `JScrollPane` (scrollable results)
- `JLabel` (suggestion text)

Layout is intentionally minimal: one input, one button, one result area, one suggestion label.

---

## 8. Example Queries
English:
- `computer`
- `"machine learning"`
- `employment /3 place`
- `comput*`

Arabic:
- `"الذكاء الاصطناعي"`

---

## 9. Evaluation
The system evaluates retrieval quality and performance using:
- **Precision**: relevant retrieved / retrieved
- **Recall**: relevant retrieved / relevant
- **Execution Time**: per-query runtime in milliseconds

Generated files:
- `results/evaluation_results.txt`
- `results/sample_queries.txt`

---

## 10. Technologies Used
- Java (JDK standard library)
- Java Swing (GUI)
- Java Collections Framework (HashMap, HashSet, List)

---

## 11. Code Quality and Design
The implementation follows:
- Object-Oriented Design
- Separation of Concerns
- Modular packages by responsibility
- Reusable methods and clear naming conventions
- Basic error handling in startup and file processing

