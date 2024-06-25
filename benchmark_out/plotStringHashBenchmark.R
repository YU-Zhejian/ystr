library("tidyverse")

df <- readr::read_tsv("StringHashBenchmark.speed.tsv")

p <- ggplot(df) +
    geom_hex(aes(x=LEN, y=TIME)) +
    scale_y_continuous(trans="log10") +
    scale_fill_continuous(trans="log10") +
    facet_wrap(.~ALGO) +
    theme_bw()
ggsave("StringHashBenchmark_vsStrLen.pdf", p, width=10, height=10)
