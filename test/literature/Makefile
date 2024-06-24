all: \
	oxford_3k.txt \
	shakespeare_gutenberg.txt \
	kjv_gutenberg.txt

oxford_3k.txt:
	curl -L https://github.com/jnoodle/English-Vocabulary-Word-List/raw/master/Oxford%203000.txt > oxford_3k.txt

wordlist.txt:
	curl -L https://websites.umich.edu/~jlawler/wordlist> wordlist.txt

shakespeare_gutenberg.txt:
	curl -L https://www.gutenberg.org/ebooks/100.txt.utf-8 > shakespeare_gutenberg.txt

kjv_gutenberg.txt:
	curl -L https://www.gutenberg.org/ebooks/10.txt.utf-8 > kjv_gutenberg.txt

validate:
	sha256sum -c sha256sum
