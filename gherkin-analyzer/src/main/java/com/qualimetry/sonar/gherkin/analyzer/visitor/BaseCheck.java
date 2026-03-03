/*
 * Copyright 2026 SHAZAM Analytics Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qualimetry.sonar.gherkin.analyzer.visitor;

import com.qualimetry.sonar.gherkin.analyzer.parser.model.TextPosition;
import org.sonar.check.Rule;

/**
 * Abstract base class for all Gherkin analysis checks.
 * <p>
 * Provides convenience methods for reporting issues at various granularity
 * levels (precise position, line-only, or file-level). Subclasses should
 * override the appropriate {@link FeatureVisitor} methods to implement
 * their detection logic and call the {@code addIssue}, {@code addLineIssue},
 * or {@code addFileIssue} methods to report findings.
 * <p>
 * The {@link #setContext(FeatureContext)} method must be called before the
 * walker invokes any visitor methods, supplying the context for the current
 * file being analyzed.
 */
public abstract class BaseCheck implements FeatureVisitor {

    private FeatureContext context;

    /**
     * Sets the context for the current file analysis. Called by the
     * walker/sensor before visiting the tree.
     *
     * @param context the analysis context for the current file
     */
    public void setContext(FeatureContext context) {
        this.context = context;
    }

    /**
     * Returns the current analysis context.
     */
    protected FeatureContext getContext() {
        return context;
    }

    /**
     * Returns the rule key from this check's {@link Rule} annotation.
     *
     * @return the rule key string, or {@code "unknown"} if the annotation is missing
     */
    protected String getRuleKey() {
        Rule ruleAnnotation = getClass().getAnnotation(Rule.class);
        if (ruleAnnotation != null) {
            return ruleAnnotation.key();
        }
        return "unknown";
    }

    /**
     * Reports an issue at a precise text position.
     *
     * @param position the position in the source file
     * @param message  the issue message
     */
    protected void addIssue(TextPosition position, String message) {
        context.addIssue(new Issue(getRuleKey(), message, position, position.line(), null, null));
    }

    /**
     * Reports an issue spanning a range on one line (e.g. to highlight a specific word).
     *
     * @param startColumn 1-based column of the first character
     * @param endColumn   1-based column of the last character (inclusive)
     * @param line        1-based line number
     * @param message     the issue message
     */
    protected void addIssue(int startColumn, int endColumn, int line, String message) {
        context.addIssue(new Issue(getRuleKey(), message, new TextPosition(line, startColumn), line, null, endColumn));
    }

    /**
     * Reports an issue at a specific line (no column precision).
     *
     * @param line    the 1-based line number
     * @param message the issue message
     */
    protected void addLineIssue(int line, String message) {
        context.addIssue(new Issue(getRuleKey(), message, null, line, null, null));
    }

    /**
     * Reports a file-level issue (no specific line or position).
     *
     * @param message the issue message
     */
    protected void addFileIssue(String message) {
        context.addIssue(new Issue(getRuleKey(), message, null, null, null, null));
    }

    /**
     * Reports an issue at a precise text position with a remediation cost.
     *
     * @param position the position in the source file
     * @param message  the issue message
     * @param cost     the remediation cost
     */
    protected void addIssue(TextPosition position, String message, double cost) {
        context.addIssue(new Issue(getRuleKey(), message, position, position.line(), cost, null));
    }

    /**
     * Reports an issue at a specific line with a remediation cost.
     *
     * @param line    the 1-based line number
     * @param message the issue message
     * @param cost    the remediation cost
     */
    protected void addLineIssue(int line, String message, double cost) {
        context.addIssue(new Issue(getRuleKey(), message, null, line, cost, null));
    }
}
