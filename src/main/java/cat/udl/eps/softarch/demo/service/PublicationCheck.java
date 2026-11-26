package cat.udl.eps.softarch.demo.service;

import java.util.List;

/**
 * Publication checklist of a test (US-EST-39): every rule of US-EST-23 as passed or failed, each
 * pointing to the exact question or field so that the client can take the author to it.
 */
public record PublicationCheck(boolean publishable, List<Requirement> requirements) {

    public static final String HAS_QUESTIONS = "HAS_QUESTIONS";
    public static final String MIN_OPTIONS = "MIN_OPTIONS";
    public static final String ONE_CORRECT_OPTION = "ONE_CORRECT_OPTION";
    public static final String OFFICIAL_PUBLIC = "OFFICIAL_PUBLIC";

    public static PublicationCheck of(List<Requirement> requirements) {
        return new PublicationCheck(requirements.stream().allMatch(Requirement::passed), requirements);
    }

    public List<Requirement> failed() {
        return requirements.stream().filter(requirement -> !requirement.passed()).toList();
    }

    /** One rule. The message names the question number or the field and what is wrong. */
    public record Requirement(String code, boolean passed, String message, Target target) {
    }

    /**
     * Where the requirement applies. type is TEST (a field of the test, named in field) or QUESTION
     * (number is its 1-based position in the test); uri is the resource to open.
     */
    public record Target(String type, String field, Integer number, String uri) {
    }
}
