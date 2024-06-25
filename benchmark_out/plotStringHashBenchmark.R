library("tidyverse")
library("pheatmap")

df <- readr::read_tsv("StringHashBenchmark.speed.tsv")

p <- ggplot(df) +
    geom_hex(aes(x=LEN, y=TIME)) +
    scale_y_continuous(trans="log10") +
    scale_fill_continuous(trans="log10") +
    facet_wrap(.~ALGO) +
    theme_bw()
ggsave("StringHashBenchmark_vsStrLen.pdf", p, width=15, height=15)

df_wide <- readr::read_tsv("StringHashBenchmark.collisions.tsv") %>%
  dplyr::transmute(RATE=ifelse(COLLISIONS==0, NA, log10(COLLISIONS/TOTAL)), ALGO, DATA) %>%
  tidyr::pivot_wider(names_from = ALGO, values_from = RATE) %>%
  as.data.frame()

row.names(df_wide) <- df_wide$DATA
df_wide$DATA <- NULL

pheatmap(
  df_wide,
  cluster_cols = TRUE,
  cluster_rows = FALSE,
  display_numbers = TRUE
)
