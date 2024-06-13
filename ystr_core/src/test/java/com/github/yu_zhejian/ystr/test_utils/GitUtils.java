package com.github.yu_zhejian.ystr.test_utils;

import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public final class GitUtils {
    private static final Logger LH = LoggerFactory.getLogger(GitUtils.class);

    public static @NotNull String getGitRoot() throws FileNotFoundException {
        try {
            var repository =
                    new FileRepositoryBuilder().readEnvironment().findGitDir().build();

            var gitRootPath = repository.getDirectory().getParent();
            repository.close();
            return gitRootPath;
        } catch (Exception e) {
            LH.error("Failed to get git root", e);
            throw new FileNotFoundException();
        }
    }
}
