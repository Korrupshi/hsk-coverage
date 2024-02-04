# TODO - Chinese Parser

## Features
[x] Which genres are best for learning HSK words
[x] Which genres are complementary to maximise HSK coverage
[x] Get HSK coverage I get when completing an entire webnovel
[x] Based on my current known HSK words, which genre/book should I read

## 1. Aim: 
### 1. What max coverage can novels provide?
Full genre = 96%

### 2. Which genres are best for most HSK coverage? 
CITY_LIFE	78
SCI_FI	76
GAMING	75
SUSPENSE	75
CAREER	75

### 3. Which genres are complementary for HSK coverage?
SCI_FI + CITY_LIFE: 87%
HISTORY + CITY_LIFE: 86%
FANTASY + CITY_LIFE: 85%
HISTORY + SCI_FI: 85%

### 4. What additional coverage does a single book give?
50913 = 3% at 150 chapters
46957 = 5% at 150 chapters

## 2. Methods
- Minimum of 100k chars for calculating HSK coverage

### HSK coverage
This is the percent of HSK1-6 words that are covered in a novel or category

- categoryCoverage results
- complementaryCoverage results <pair-wise categories>
- Single books coverage

