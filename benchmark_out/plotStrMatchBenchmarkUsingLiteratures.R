library("tidyverse")

df <- readr::read_tsv("StrMatchBenchmarkUsingLiteratures.tsv")

p <- ggplot(df) +
    geom_boxplot(aes(x=NeedleLen, y=TimeNS, group=NeedleLen)) +
    scale_y_continuous(trans="log10") +
    facet_wrap(Text~Algorithm) +
    theme_bw()
ggsave("StrMatchBenchmarkUsingLiteratures_vsStrLen.pdf", p)
