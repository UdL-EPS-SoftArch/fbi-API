# <!-- gdoc_comment_begin: c1 -->**Platform Roles, Features and User Stories<!-- gdoc_comment_end: c1 -->**

## **1\. Role Model**

The platform has three main roles, organized hierarchically:

* **Visitor:** unauthenticated user with read-only access to the public area of the platform.  
* **Student:** authenticated user who, in addition to public features, can take tests, track progress, participate in leaderboards and create practice content.  
* **Administrator:** user with all the features available to a student, plus additional management, moderation, validation and monitoring permissions. When an administrator takes an official test, their score does not count toward the leaderboards.

Any public feature available to a visitor is also available to a student and an administrator, and general student features can also be used by an administrator as long as they do not interfere with leaderboard integrity.

## **2\. Platform Roles**

### **2.1. Visitor**

**Brief Description**

A visitor is an unauthenticated user who accesses the public area of the platform. Their main goal is to understand the application's content and functionality before registering. They can explore subjects, tests, leaderboards and public profiles, but cannot perform actions that require user identification or modify information.

**What They Can Do&nbsp;**

* View the home page, featured tests and a summary of the leaderboards.  
* Browse the available subjects.  
* Explore the catalog of publicly visible published tests.  
* Search and filter tests by subject.  
* View the <!-- gdoc_comment_begin: c2 -->public details<!-- gdoc_comment_end: c2 -->(Title and Year) of a test.  
* View public subject and global leaderboards.  
* View users' public profiles.  
* Register, log in and recover a forgotten password.

**What They Cannot Do**

* Start or submit tests.  
* Obtain scores or participate in leaderboards.  
* Create, edit, delete or publish tests.  
* Report content of the website.  
* View a personal results history.  
* Access other users' private data.  
* View correct answers before completing a test.  
* Access student-only or administrator-only features.

**Behavior When Attempting a Restricted Action**

If a visitor attempts an action that requires authentication, such as taking a test, creating content or reporting it, the system must inform them that an account is required and provide direct access to registration or login. After authentication, the user should return to the point from which the action was started whenever possible.

**Security Considerations**

* Restrictions must be enforced on the server, not only in the user interface.  
* Visitors must never receive private data or restricted test information.  
* <!-- gdoc_comment_begin: c3 -->Public requests <!-- gdoc_comment_end: c3 -->may be subject to *rate limiting* to reduce large-scale data extraction.

**Role Objective**

Allow users to understand the value of the platform without entry barriers and make it easy for visitors to become registered users.

### **2.2. Student**

**Brief Description**

A student is a registered user, mainly a student at the University of Lleida, who uses the platform to prepare for subjects through tests. Students have access to all public visitor features and can also take official tests, obtain scores, participate in leaderboards, review their history, earn rewards and create custom practice tests.

**Official tests** can generate competitive scores that affect leaderboards. Once a score is recorded, it remains fixed, meaning that any future changes to the exam's scoring criteria will not affect previously obtained scores.

**Custom tests** <!-- gdoc_comment_begin: c4 -->created by students themselves, who write their own questions and answer options, primarily for practice. Their scores do not contribute to the competitive scores used to rank students on the leaderboards<!-- gdoc_comment_end: c4 -->

**What They Can Do**

* Perform all public actions available to visitors.  
* Browse available subjects and tests.  
* Take official tests.  
* View their scores, correct answers and mistakes.  
* View test, subject and global leaderboards.  
* Create, publish and manage custom practice tests.&nbsp;  
* View and filter their test history.  
* Earn rewards (*badges*) and view their requirements.  
* View and update editable profile information.  
* View other users' public profiles while respecting privacy rules.  
* Report incorrect or inappropriate content and submit issues about the platform.  
* Log out.

**What They Cannot Do**

* Manually modify obtained scores.  
* Directly modify leaderboards.  
* Obtain competitive scores through custom tests.  
* Modify their results history.  
* Assign or manually modify their own rewards.  
* Access private data or detailed results belonging to other students.  
* Manage or create subjects, official tests or other administrator-only features.

**Security and Integrity Considerations**

* Official test scores must be calculated and stored automatically.  
* Test submission must require confirmation before answers are graded definitively.  
* Other users' private data and detailed results must not be accessible.  
* Students must not be able to directly manipulate actions affecting scores, history or rewards.

**Role Objective**

**<!-- gdoc_comment_begin: c5 -->**Provide a practice and self-assessment tool that allows students to prepare for subjects, analyze their progress and participate in a competitive component based on reliable results.<!-- gdoc_comment_end: c5 -->

### **2.3. Administrator**

**Brief Description**

The administrator is responsible for managing, moderating and maintaining the platform. Administrators have visitor and student features, as well as access to an administration panel from which they can manage subjects, official tests, questions, custom tests from students, issues, leaderboards and statistics.

When an administrator takes an official test for validation purposes, the system may calculate and display the result, but that score must not be included in the leaderboards. Although administrators have access to student features, <!-- gdoc_comment_begin: c6 -->their accounts are not publicly discoverable, and they cannot earn student badges<!-- gdoc_comment_end: c6 -->.

**What They Can Do**

* Perform actions available to visitors and students, except where competitive participation is restricted.  
* Access the administration panel.  
* Create, edit and delete subjects.  
* Manage subject information and associated tests.  
* Create, edit, publish, withdraw and delete official tests.  
* Save official tests as drafts.  
* Add, edit, delete and reorder questions.  
* Define possible answers, the correct answer and the score for each question.  
* Preview and take official tests for validation purposes.  
* Verify automatic grading and score calculation.  
* View and manage leaderboards when there is a<!-- gdoc_comment_begin: c7 --> justified administrative reaso<!-- gdoc_comment_end: c7 -->n.  
* <!-- gdoc_comment_begin: c8 -->Moderate tests deliberately<!-- gdoc_comment_end: c8 --> and <!-- gdoc_comment_begin: c9 -->other content <!-- gdoc_comment_end: c9 -->created by students and remove those tests in case it contains inappropriate content  
* Review reported content and submitted issues.  
* Correct content and mark issues as resolved.  
* View general, subject-level and test-level <!-- gdoc_comment_begin: c10 -->statistics<!-- gdoc_comment_end: c10 -->.  
* &nbsp;

**Administrative Behavior**

* Destructive actions or actions that may affect other users must require confirmation.  
* Official tests can be prepared as drafts and validated before publication.  
* Changes to a test that already has recorded results must be handled without creating inconsistencies.

**Role Objective**

Ensure that the platform remains reliable, consistent and secure through content management, validation of official tests, moderation, issue resolution and overall system monitoring.

### **2.4. Other Relevant Information and Domain Rules**

The following rules expand the product decisions in this specification and establish a shared reference for implementation. Confirmed project decisions include the two leaderboard types, personal-best scores, current eligibility of published official tests, noncompetitive custom tests, permanent submitted scores, self-service publication of custom tests, proactive moderation and the intended author notification after moderation withdrawal.

Specific values or workflows not stated in the original requirements—such as precise report states, attempt statuses and initial grading restrictions—are presented as proposed implementation conventions. They must be reviewed and approved by the team before AI agents treat them as final acceptance criteria. Agents must not invent incompatible alternatives or silently change the class diagram. Features marked \*future scope\* are not part of the initial release.

**&nbsp;2.4.1. Reporting Test Content and Platform Problems**

Authenticated users can submit three distinct kinds of report through the same reporting workflow and the existing \`REPORT\` entity:

\| Report target \| \`REPORT.test\_id\` \| \`REPORT.question\_id\` \| Example \|

\| \--- \| \--- \| \--- \| \--- \|

\| Entire test \| Required \| \`NULL\` \| Offensive test title, misleading description, duplicated test \|

\| Specific question \| Required \| Required \| Incorrect answer, offensive question, ambiguous wording \|

\| General platform problem \| \`NULL\` \| \`NULL\` \| Page does not load, interface error, broken platform feature \|

For a question-level report, the referenced question must belong to the referenced test. A report must not refer to a question without a test. These rules require \`test\_id\` and \`question\_id\` to be nullable in the database where appropriate; the class diagram's foreign-key notation does not by itself determine nullability.

All reports require an authenticated reporter (\`reporter\_id\`). Visitors may open a public \*Report Problem\* information page but must log in before submitting anything. The form should make the target clear, request a predefined \`reason\` and allow an explanatory \`description\`.

Proposed status convention (requires approval): the allowed report statuses are \`pending\`, \`in\_review\`, \`resolved\` and \`dismissed\`. A new report starts as \`pending\`; administrators may review it, make corrections, withdraw content, resolve it or dismiss it with a documented reason. \`resolved\_by\` and \`resolved\_at\` remain \`NULL\` until the report is closed as \`resolved\` or \`dismissed\`. Users may consult the status of their own reports; only administrators may modify the resolution fields. Reporting does not by itself remove content.

**&nbsp;2.4.2. Leaderboard Types, Eligibility and Score Calculation**

There are exactly two leaderboard types in the initial release:

1. Subject leaderboard: ranks students by their combined personal-best scores across eligible official tests associated with that subject.  
2. Global leaderboard: ranks students by the sum of their subject-leaderboard scores across all subjects.

There is no separate test-level leaderboard. A test can still display a student's personal-best score and attempt history; those are personal results, not a third ranking type.

An attempt is eligible for competitive calculation only when all of the following conditions hold: it belongs to a student rather than an administrator; it is a completed, submitted attempt of an official test; the test currently has \`status \= published\` and \`is\_public \= true\`; and the attempt was created through the regular test-taking workflow rather than administrative validation. The server sets \`counts\_for\_leaderboard\`; clients cannot set it. Custom, preview, validation, unfinished and abandoned attempts never count.

Personal best per test. If a student takes an eligible official test multiple times, their contribution from that test is the highest score ever stored in a submitted attempt, not the sum of all their attempts. Taking the test again only increases the contribution if the new attempt has a strictly higher score. An equal score does not replace the earlier best attempt for tie-breaking purposes.

Subject calculation. For each eligible official test in the subject, take that student's highest submitted score, or zero if they have never completed the test, and add those values together. For example:

\| Official test \| Highest submitted score \|

\| \--- \| \---: \|

\| Exam 2023 \| 75 \|

\| Exam 2024 \| 90 \|

\| Exam 2025 \| 80 \|

\| Subject total \| 245 \|

Global calculation. Sum the student's current subject totals. For example, scores of 245, 180 and 120 across three subjects produce a global score of 545\. Scores are added as stored, without normalizing for test difficulty or maximum possible score. This is an intentional ranking rule, including its effect that subjects with more or higher-point official tests can contribute more to the global total.

Ranking and tie-breakers. Rank entries using the following order, applying the next criterion only when the preceding one is tied:

1. Higher total score ranks first.  
2. Fewer submitted attempts needed to reach the personal bests ranks first.  
3. Lower cumulative time spent on those attempts ranks first.  
4. If everything remains tied, display the same rank for those students. A deterministic secondary display order, such as \`username\`, may be used without changing the shared rank.

To make the second and third criteria reproducible, calculate them \*per official test\*. Count the student's submitted attempts from their first submission up to and including the first attempt that achieved their current highest score. Sum those counts across the eligible tests for the subject. For elapsed time, sum the durations of those same submitted attempts, measured from \`started\_at\` to \`submitted\_at\`. A student's subsequent equal-scoring attempts do not improve their tie-breakers. If the student later achieves a strictly higher best score, the new best and its corresponding number of attempts and cumulative elapsed time become the basis of the ranking.

For global ties, aggregate both tie-breaker totals across all eligible official tests in all subjects. Leaderboard entries are calculated only for students with at least one eligible submitted attempt; a missing test contribution is worth zero, not an additional attempt. Attempts deliberately abandoned without submission do not count as submitted attempts and must not be treated as scored; safeguards against repeated abandonment can be added separately if abuse appears.

Withdrawal and republication. If an administrator withdraws an official test, it becomes ineligible immediately: its contribution, and the tie-breaker values associated with that test, are removed from affected subject and global leaderboard calculations. Attempts are not deleted, and students can still see their previously earned results in their private history. If the same official test is later republished without replacing its identity, eligible historical attempts contribute again using their stored values. Recalculations must be automatic and consistent, rather than implemented as arbitrary edits to leaderboard ranks.

Lifetime rankings and future resets. The default rankings are lifetime rankings and do not reset periodically. An exceptional administrator-initiated reset or the display of previous active periods is \*future scope\*. The existing \`LEADERBOARD\` fields do not identify periods or retain snapshots; a genuine reset/history feature therefore requires an explicitly designed persistence strategy before activation. The interface must not offer a reset that silently discards earlier results. If such a feature is implemented later, it must show each recorded period's start and end dates and preserve historical attempts.

**&nbsp;2.4.3. Test Attempts, Submission and Automatic Grading**

Both official and custom tests can be taken and graded automatically. Only eligible official-test attempts affect the competitive rankings. The \`TEST\_ATTEMPT\` and \`ANSWER\_ATTEMPT\` records also support private progress tracking and result review for custom tests.

Proposed attempt-status convention (requires approval): for the initial release, \`TEST\_ATTEMPT.status\` has the values \`in\_progress\`, \`submitted\` and \`abandoned\`. \`started\_at\` records when the attempt begins; \`submitted\_at\` is filled only on successful submission. Submission requires confirmation. After submission, the attempt's recorded result must be immutable to ordinary users. An abandoned attempt cannot be resumed as a submitted competitive result without a separately defined resume feature.

Proposed attempt-type convention (requires approval): \`TEST\_ATTEMPT.attempt\_type\` describes the execution context, with recommended values \`regular\` and \`validation\`; the official/custom distinction is derived from the linked \`TEST.test\_type\`, not duplicated in \`attempt\_type\`. Administrative previews or validation runs use \`validation\`, cannot become competitively eligible, and may be stored separately in attempt history or excluded from student-facing analytics as appropriate.

Proposed initial grading rule (requires approval: single-choice questions). Each question has at least two answer options and exactly one correct option. A correct response earns \`QUESTION.score\`; a wrong or unanswered response earns zero, with no negative marking. \`TEST.max\_score\` is the sum of all question scores and must be updated when question scores change. The submitted attempt's \`score\` is the sum of its recorded \`ANSWER\_ATTEMPT.awarded\_score\` values; \`correct\_count \+ incorrect\_count \+ unanswered\_count\` equals the number of questions in the submitted test. A question not answered at submission is recorded with \`selected\_option\_id \= NULL\`, a zero awarded score and no actual answer timestamp. For questions that are answered, the selected option must belong to that question. The initial release supports one selected answer per question; adding multiple-selection questions would require revisiting the current answer-attempt model.

Because the model stores integer \`TEST\_ATTEMPT.score\` and \`QUESTION.score\`, initial scoring uses non-negative integer values, although \`ANSWER\_ATTEMPT.awarded\_score\` is stored in a decimal-capable field. Grading, correctness and submission timestamps must be computed on the server. Before a non-author student submits a test, the API must not reveal its correct options.

**&nbsp;2.4.4. Test Ownership, States, Visibility and Publication**

\`TEST.test\_type\` distinguishes \`official\` and \`custom\` tests. Only administrators create or maintain official tests; students can author their own custom tests. Administrators may also create custom tests for practice.

\`TEST.status\` accepts \`draft\`, \`published\` and \`withdrawn\`. This field describes the lifecycle of the test. \`TEST.is\_public\` independently describes whether a published test is visible to other users. The combination of the two fields is interpreted as follows:

\| \`status\` \| \`is\_public\` \| Meaning \|

\| \--- \| \--- \| \--- \|

\| \`draft\` \| \`false\` \| Work in progress, visible to its author and authorized administrators only \|

\| \`published\` \| \`false\` \| Private custom test; its author can use it, other students cannot browse or take it \|

\| \`published\` \| \`true\` \| Publicly visible test; authenticated users can take it and visitors can inspect public details \|

\| \`withdrawn\` \| Either \| Unavailable for new regular attempts; retained for author/admin review and historical integrity \|

Official tests must be publicly accessible when published (\`status \= published\`, \`is\_public \= true\`). A published official test with \`is\_public \= false\` is not a valid normal public-release state. Draft tests are not publicly listed regardless of any inconsistent stored visibility flag; when a test is first saved as a draft, \`is\_public\` should default to \`false\`. Withdrawal overrides the public visibility flag.

Authors may save custom tests as drafts, make them private, publish them publicly and move a previously public custom test back to private visibility. Custom-test publication does not require prior administrator approval. As a proposed minimum validation policy, it requires: a subject, title, at least one valid question, at least two options per question, exactly one correct option per question, and valid non-negative integer scores. Administrators moderate inappropriate content through reports or proactive review and may withdraw or hide a custom test when necessary. Public access to a test never means public access to its correct answers before submission.

**&nbsp;2.4.5. Edits, Deletion and Historical Score Integrity**

When a test already has submitted attempts, previously submitted scores are preserved if an administrator changes the score assigned to a question. The updated question score changes the current test maximum and applies to new submissions, but old \`TEST\_ATTEMPT.score\` and \`ANSWER\_ATTEMPT.awarded\_score\` values are not recalculated automatically. Users are not automatically notified of such a score change. Past results remain viewable in their private history.

This rule is intentionally different from regrading existing submissions. It also introduces a fairness concern: historical and new scores may be based on different point distributions. Administrative edits should therefore be clearly audited, and a material change to an official test's scoring or correct answers should normally lead to withdrawal and publication of a new test record rather than silent mutation of an active competition. With the current model, the system cannot reliably reconstruct the exact old text/options of changed questions: immutable attempt scores are supported, but full question-version history is not. If full historical reconstruction is required, explicit versioning or answer-content snapshots must be designed before implementation.

A test with stored attempts must not be physically deleted, because doing so could destroy results, reports or ranking evidence. Instead, withdraw or hide it while preserving its records. A test that has never been attempted may be deleted subject to referential-integrity checks. A subject with associated tests must not be physically deleted until those relationships have been handled under an explicit archival or deletion policy.

**&nbsp;2.4.6. Badges and Rewards**

Administrators create and maintain badges through the \`BADGE\` entity. Each badge has a name, description, icon, numeric \`requirement\_value\`, \`requirement\_type\` and active/inactive state. A requirement identifies an objectively measurable achievement. Illustrative, not yet approved requirement types include reaching a threshold of completed official tests, obtaining perfect scores in official tests or reaching an official-score milestone. Only requirement types the system explicitly supports should be selectable; the numeric field must not be interpreted as an arbitrary code expression.

Badges are earned automatically through official-test activity only. Custom tests do not award badges in the initial release. After a qualifying official submission, the server evaluates active badges and awards each qualifying badge at most once per user, by creating a \`USER\_BADGE\` record and setting \`earned\_at\`. Neither student users nor administrators may arbitrarily edit earned rewards to manipulate achievements. Administrators may disable a badge to prevent future awards; previously earned badges are retained unless a separately authorized correction is required.

An earned badge can be shown or hidden on the public profile through \`USER\_BADGE.is\_visible\`. Its details and requirements are available for viewing even before it is earned. Withdrawal of an official test removes its competitive leaderboard contribution as described above; previously earned badges remain as historical achievements unless a specific future policy explicitly provides for revocation.

**&nbsp;2.4.7. Public Profiles and Privacy**

Each registered user has a public profile identified by their \`username\`. The username must remain visible as the public identifier used by published content and ranking entries. The user can decide which optional personal information and achievements to display publicly, subject to what the platform can securely store and enforce.

The intended public-profile fields include the username, optionally the full name and email address, publicly published custom tests, earned badges marked visible, and current subject/global ranking positions. A user's personal test-answer history, private custom tests, password hash, private contact information and administrator-only information are never exposed merely because their profile is public.

Privacy defaults: email and full name are hidden unless the user explicitly opts in; individual badges follow \`USER\_BADGE.is\_visible\`; only published and public custom tests are listed. Showing an email publicly requires a clear opt-in with a warning that it becomes accessible to others. Students may not inspect other users' private attempt details.

Schema limitation: \`USER\_BADGE.is\_visible\` already persists badge visibility, but the class diagram does not include persistent profile settings for public email/full-name preferences. The UI must not claim those choices are saved until suitable server-side preference storage is defined. In the meantime, keep email and full name private. Any implementation of editable profile visibility must be enforced server-side, including API responses.

**&nbsp;2.4.8. Administrative Statistics and Monitoring**

Administrators should have a statistics dashboard with concrete, meaningful measures derived from existing entities rather than arbitrary manually maintained counters. The initial set includes: totals of users and subjects; published official and custom tests; drafts and withdrawn tests; attempted and submitted tests; unique participating students; official versus custom attempts; average, highest and lowest stored test scores; question-level counts of correct, incorrect and unanswered submissions; participation by subject and test; and counts of pending, in-review, resolved and dismissed reports.

Where applicable, dashboard filters may include subject, test, official/custom type and date range based on stored timestamps. A public statistics view must not inherit the administrator's access to private answers, email addresses or individual student histories. Administrators use aggregate metrics to detect low-quality content and technical issues, not to change scores directly.

**&nbsp;2.4.9. Moderation and Author Notifications**

Administrators may proactively review published custom tests and withdraw inappropriate, inaccurate or policy-violating content even when nobody has filed a report. Moderation withdrawal changes the existing test's \`status\` to \`withdrawn\` and blocks new regular attempts, while preserving previous submissions and reports. This is different from physically deleting the test record, which is restricted when dependent history exists.

When an administrator withdraws a student-authored custom test, the intended product behavior is to notify its author, identifying the affected test and explaining the moderation action. This is covered by US-EST-34 and US-ADM-32. By contrast, a scoring change to an existing question does not automatically notify students who took the test; their recorded scores remain unchanged.

Design dependency: The present diagram has no notification queue, unread state, delivery channel or moderator reason field. Test withdrawal can be implemented with existing fields, but a reliable notification with explanatory text cannot be guaranteed from \`TEST.status\` alone. Before implementing those notification stories, approve an in-app or email delivery design, define where the reason and event are stored and clarify delivery failure handling. Until then, the UI can truthfully display \`withdrawn\` on the author's test page but must not claim a notification was sent.

**&nbsp;2.4.10. Data Integrity Rules and Model Interpretation**

The class diagram is the current data-model reference. To avoid accidental contradictions between multiple implementations:

\- \`LEADERBOARD\` represents ranking entries for users, not a single leaderboard object. An entry belongs to a user and has one \`leaderboard\_type\`: \`subject\` or \`global\`. Subject entries reference \`subject\_id\` and leave \`test\_id\` null; global entries leave both \`subject\_id\` and \`test\_id\` null. Only one current entry per user and scope is permitted. Test-specific leaderboard entries are not generated in the initial release.

\- \`ANSWER\_ATTEMPT\` reaches its user through \`TEST\_ATTEMPT.user\_id\`; no direct \`ANSWER\_ATTEMPT.user\_id\` column is required.

\- One question can have multiple options, but a submitted answer selects at most one option. For a given attempt and question, at most one \`ANSWER\_ATTEMPT\` record is permitted.

\- A test must be associated with one subject and one author. An administrator's official-test submissions may be graded but must never be included in rankings.

\- An active leaderboard entry can be recalculated from authoritative, eligible stored attempts. A rank or score in \`LEADERBOARD\` is derived data and must not be hand-edited as a normal operation.

\- Server-side authorization, ownership checks, grading and field validation are mandatory; hiding a button is not sufficient security.

## **3\. General Functional Consistency Rules**

1. **Permissions:** Students inherit public visitor features. Administrators inherit those features and non-competitive student workflows, but administrative validation never earns leaderboard points.  
2. **Two test types:** Only administrators create official tests. Students and administrators can create custom practice tests. Both test types can be automatically graded; only eligible official attempts influence rankings.  
3. **Two ranking types:** Only subject and global leaderboards exist in the first release; they use each student's best eligible result per official test. Score and tie-break calculations follow Section 2.4.2.  
4. **No routine resets:** Rankings are lifetime totals. Exceptional period-based resets are a future feature requiring explicit historical persistence.  
5. **Publication:** Lifecycle \`status\` and public visibility \`is\_public\` are separate concepts. Custom-test publication is self-service after validation; administrators can moderate or withdraw content.  
6. **History integrity:** Submitted results are retained if tests are modified or withdrawn. Old scores are not silently regraded. Physical deletion must not destroy attempts, answers, leaderboard evidence or reports.  
7. **Privacy:** Username is public; optional profile details require consent and persistent privacy settings. Private attempts, private tests, email (by default) and password hashes remain protected.  
8. **Badges:** Administrators define badges; the platform awards them automatically based on official-test activity. Students may control earned-badge visibility but cannot arbitrarily assign achievements.  
9. **Reports:** Only authenticated users can submit reports. Test-level, question-level and platform-level reports use the nullable identifiers and status workflow in Section 2.4.1.  
10. **Security:** Permissions, ownership, answer secrecy, grading, eligibility and state transitions are always enforced on the server.

## **4\. Implementation Dependencies and Future-Scope Decisions**

This section explicitly separates the agreed functional behavior from capabilities **not yet fully supported by the current class diagram**. AI agents must not silently fabricate tables, redefine the ranking system or declare the following features implemented without an approved design decision.

\| Item \| Current position \| Implementation requirement \|

\| \--- \| \--- \| \--- \|

\| **Operational enumerations** \| The source diagram has generic string fields for report state, attempt state and execution context. \| Approve the proposed allowed values before persisting data or generating migrations; never infer arbitrary state values. \|

\| **Initial grading constraints** \| Single-answer, non-negative integer scoring with no penalties is proposed for the first release. \| Confirm question formats, minimum option count, score rules and handling unanswered questions before enforcing them. \|

\| **Moderation notifications** \| Authors should receive a notification if moderation withdraws their custom test. \| No notifications or recorded moderation reasons exist in the provided class diagram. Agree on delivery, persistence and reason capture before fulfilling US-EST-34 / US-ADM-32. \|

\| **Profile privacy preferences** \| Users should opt in to public email/full name. \| No profile-visibility settings exist in \`USER\`; add persistent preferences or defer this functionality. Keep email/full name private until supported. \|

\| **Historical leaderboard periods / resets** \| No scheduled resets. Exception-only reset is future scope. \| To retain previous active periods and their date ranges, introduce an approved period/snapshot strategy; \`LEADERBOARD.calculated\_at\` alone is insufficient. \|

\| **Historical question versions** \| Previous numerical scores are preserved after question-score changes. \| Existing tables do not preserve previous question/option text. Require versioning/snapshots if exact old test reconstruction becomes necessary; meanwhile, avoid substantive edits to attempted official tests. \|

\| **Question format** \| Initial release uses one correct answer and one selected option per question. \| Multiple-selection or free-text questions need a revised response model and grading rules. \|

\| **Abandoned attempts / timing abuse** \| Only submitted attempts count in the specified tie-breakers. \| Apply rate limits and protections to deter deliberate abandonment. A separate anti-abuse policy is needed if abandoned attempts are to affect competition. \|

\| **Score normalization** \| Subject and global leaderboards use raw stored scores. \| Changing to percentages, averages or weighting requires an explicit policy change and migration/recalculation, not an agent assumption. \|

**Source-of-truth order:** This document's Section 2.4 defines the business behavior; user stories describe required interactions under those rules; the class diagram defines existing stored fields and relations. When the two cannot express the same behavior, record the mismatch as a design dependency rather than silently changing either one.

&nbsp;

---
gdoc_comments:
  c1:
    status: ACTIVE
    token: AX5pRSlwylP_5UjHHX_Qnlh5zJOH2wXQxEsdL1ulmAe2H4WDkDUI_k0MT0zgO_R-vWMxh6JTukDlc01gOzKZCFVYNxpxFf57PYQogT6DXcBteyA-7VYP7uwVBJ1aGFkueiTFVXOiQ1RAQ8u8bO2LRhu7vcl_frQJ5Xl7Z4XKbEn3hezZb1ARc0CMJwdxea1fbSYh2OxSwr1nRQccA9SBhdEJ-eiSDSBUvS7Y-fSjc90_v9cw9k3-bBgdhsLT3-_M6ZzfIz8xVSPlWwJ0WfbuCAdC_w0oDnRYUTPnxFdG26R6abqvDUafdAKpLibVmfpr6CxgN9mz65uNxwhdaDD1EPup3Gvt_j9pOFikANoqsF3mrExlDq7EWz-Dsbre19OhqA-A_rt9_N6jcmD6O9oQt1FX8wY6pQCezOc-vS9dAv6FU_BdtmNXERCzhV2fwiV7BHBQ8zsHHDLXhd1YCBRtA5e7D9TtOWokZNOnR8O4qFU6yWLqxdes3v0zNnb6MdjXh1zym3HBDzhBDeI0OBS4lRSh8B5R8Z4c_1GWjRiBM0w1aOLVzj1cO_g44VsWoHxMN-8aO0PPpOmAWi9vH6kFgFJjXN2pX5jocUp8CxhUSjm3xa__eSuSwMU9H4n-KlmJY8VdWyNlE0zx5Y9FK6D4vU6TM81bi-uTBtTeGzlBO40_qGt5AeMWqoUVtg00NvXrYTijMTHVB6ZwfCPsS_UtALVUBco5vkYeWvY3S9Cv5n4tlNnmUgaJRuUoDNp36X-aebzSk-rJYw
    head:
      author: Natalie Jaya
      time: '2026-10-29T14:20:36.046Z'
      body: bug<br>Something isn&#39;t working<br><br>chore<br><br>documentation<br>Improvements
        or additions to documentation<br><br>duplicate<br>This issue or pull request
        already exists<br><br>enhancement<br>New feature or request<br><br>fix<br>Fix
        on the codebase<br><br>good first issue<br>Good for newcomers<br><br>help
        wanted<br>Extra attention is needed<br><br>invalid<br>This doesn&#39;t seem
        right<br><br>question<br>Further information is requested<br><br>wontfix<br>This
        will not be worked on
      quote: Platform Roles, Features and User Stories
  c2:
    status: ACTIVE
    token: AX5pRSkQZVyXJ3j3ljpRAlmZW2ioQcn4Tq3FRTG7q65v1u22qTAJosyAysfrUBN_vWdgoKtYCp0Ds69cTtvxy-U55kBeyoDEgg5POnxE7mAo7-_BZWk98Yoaj3dTKjEPoUZkHcOYn6X8PY05otoTi8QLvJIJ-oswitY4Bmkz61oFQgzbrsfuz6ZMyH0lokh1pAVPw_mtHWII-crsURtcj6pEaDSIDNVkWJIKbLB920hGtNrrCGVsIossquLz_PhlZNwlFsGtwTpc4Ep0JH6lVc3JJxmL2vKa77HmhBZ6kRviVc2CXuvwAuoe77--RQt-wBgVYFkqx_rzDUkR3TBcRsLMvWzVquMxWV1HonU22D-VblLdXc5kVyyhc-3sd_Ffwl-EBTkk7WHFvnRB27AfvzreBI6GfjsJtIr0YuCM8s1_st6cc_ojgk44MtheCh52d3F7WNgMLaTnKh0SiK8K9A
    head:
      author: Natalie Jaya
      time: '2026-10-25T22:20:17.396Z'
      body: definiria que son los detalles publicos de un test. Acorde a la bdd te
        compraria; Title, description (que es la descripción de un test jaja), year,
        max score i (publish &lt; updated)
      quote: public details
  c3:
    status: RESOLVED
    token: AX5pRSkBLvf0mfoKSKJpta6xZT1BjMAKFmUNJvppWpeo5eYO-WFOS2CZ7KVK4Mw27SjIiRMDfOwTdQ0jEL_WNXMys_WhA14gZFuamwY54IKSZyTxqDX5tOOvfPFpk9rI4n1l3jxBpN4ty8e0C4UZ22hhxwQyxmmJELApLp1CSdXq0pnBNHUWruYohLtF0Z_EwVBhaQlVGJdgKZVZ7hjLIqKaSM6CmA4HjPa0L2VpQE086N5VeMkEhLHUqbJJPtwSXgoGEPD4gt2z9l-5nV8tgcT6s2ZC4cnK6MU
    head:
      author: Natalie Jaya
      time: '2026-10-22T13:08:26.257Z'
      body: que entenem com a public request
      quote: 'Public requests '
    replies:
    - time: '2026-10-22T13:08:26.258Z'
      action: RESOLVE
  c4:
    status: ACTIVE
    token: AX5pRSlfcHU1U_qqdOqDWwfzfVqwAlxBlB1G6Xx8xn07U0WP_rMx-wqOZn6al0jOv9YHrCOg1CqaTfsTPHYY91hYZz4HOIYeb7J2ds1KTcQC8Euc1RA2aEatpi25V2vIZ4fX3GdUsR1PqT1M99Jld2um2o_ALLOHFpXG5p_2BEadwZEpgkQuaYMo9iyEdcria1NEanppeWWF5ggRoXkIs-4tg7OtkMXEEVvbqhZ5fhEEXH4S2YN10ycmE0nZEyVG9euGoxPr_oiwYm5ymgaJtW6T7CMzJfFbGXkZBW6kjZsWgqLBICW0D0XHBMRjyCVLT2ZjlxaX4faD3P7NGyzy6MemMPdBJ0d_DEPNqouUbeivhW_jXixxMkcUTk8u8TkMUkDwpVtMB9MWzCf8c2htaL_VvP8u22V9ORm6CImmtljeeu3S9psJvpb1zl2nvrw5A-BV50XG-A4t92Kbbmkkx9tprAKeCNXULSYq
    head:
      author: Natalie Jaya
      time: '2026-10-26T07:15:52.029Z'
      body: aqui un poco mas explicado
      quote: created by students themselves, who write their own questions and answer
        options, primarily for practice. Their scores do not contribute to the competitive
        scores used to rank students on the leaderboards
  c5:
    status: ACTIVE
    token: AX5pRSkLP2QIXdp0H0wrLZIaTDn6yXJOf5mHBBQ7yW3gCf_dILZb4U72jTMV2jiNunAPHzpcYy02Qy8id450FDydiyFbtdc4i9TdDeLVYtoSIk_zs8--8cBsQxY57bzfEacTXsmAxPHTGNdEYSNItAZ3L30GuTL9XL5KzbGyAQueQ1KUpCEmm8vmTOUn5ZYSljsD6zAgbpGWNDnPoyz4BgCmQmIy7r9fj7B8WRCQhtVnC1fsxn2gopR7B1CEsXqEPwfVHy4lIyPPTqPj1xbtrGTnTLBPeFBH0cQbrUbG8L0tHChA-oH5OhIePb_zogdYjnW-XT76Nt7pskayhajY-SG1ZcxXalnUzFLJvadZfJvrUeEzKCseqmuAtf6hSMd5ytWxFJBA2IncmoXsAeycwBGrCYe7BGBomyMQzwVNUkv13dNMGi_X8as-2RUBe6EG7QrzLLfc4xGZZTi1rPQyItd23-FkKez0shcvcm04ksjv2kIF8CRv_DxFMj7KMos
    head:
      author: Natalie Jaya
      time: '2026-10-26T07:17:32.479Z'
      body: quedaria pendiente si ponemos lo de test recopilatorios de fallos x asignatura
      quote: Provide a practice and self-assessment tool that allows students to prepare
        for subjects, analyze their progress and participate in a competitive component
        based on reliable results.
  c6:
    status: ACTIVE
    token: AX5pRSmCqE8I1pCd4ORmn-_HI9pJ9beoAYQWu63_cvxU21hTH2y2KVX2KcdSMOzsDk6UdpcJP-WwjByN2mhqqAPoxZguHfriPIr5yRVBfy9Zdi2VlBU5eLqufVT_d9hWQrelb9DADCblvCbKgn73jlP3yQ-BJv-kvC-pQmPQTPkRGIrkVxHc3nXK114RLa1Gs0K9driYG1hdQsyRdPu6fgAVlGbRd_FWvTrZudc_zzDP6lESzzvXthWKAz2oVJHmytnM2chnfgChYXGRCeNZyv4r9CoXcBs6-mIfX57qayc5FUMJITKBsswjVCTFfS76iJJcKa7BgmCV8rVM3GXcz6RozCl_l5qzh8h72JYSSKap9IXIfBH9Rj5F9hWVylWkAE_udOhxYss
    head:
      author: Natalie Jaya
      time: '2026-10-26T13:29:02.711Z'
      body: mi pequeña aclaracion, bancan shat
      quote: their accounts are not publicly discoverable, and they cannot earn student
        badges
  c7:
    status: ACTIVE
    token: AX5pRSkmFaPuIg0GHxpgpqgAhs4jfWwjOr_jTZ3wgQMMPsMS62O3F3rCyM_5v3lsxYntHv-4nkPsXTibGeGMK19hvGuV7SvqdvFmmyVr3d6sIGa6Irta3zEUKWXL2kYeoRAKUNzpf1ABSt57CKUIinV9LSLMNoUf8C937oJgynY2LzYwKg4SCuf92StnZ00WitvdVPexjiqc-3wZINUXolIYyL9Jod1zZi1iNmxCs6n7587Y2JVNXreX5AeKeFOQlEVGCV40vIyOy1J88YTLZgcdvCZTBSOpUOh3cdYLvsCNxF6dmki7IaOVObRnkVN7UE_t1PzjRnN63eyRp5HtBYmhrT7F_m4IWXqKpcL3wY-bKTD3mb9jRa4Z8jE-ih5z7GKnufPhmfYto8Fj2BvWodUsOkTKDmBHYP2adZDfrj28woTTrch8yXsaBwiyZEpUX6ASEWzAKh_j
    head:
      author: Natalie Jaya
      time: '2026-10-26T13:42:09.860Z'
      body: Entendemos que no pediremos una justificación formal si un administrador
        ha cambiado por ejemplo la posición de un jugador, entendemos que hay un motivo
        valido y administratiov tras la decisión
      quote: justified administrative reaso
  c8:
    status: ACTIVE
    token: AX5pRSngObZ_QH0HbdLLLMyjvu57qGSg4SFwr7SwT1dSuhpelglI0Ac7zGjEFXFdEr_SVpdq9eCstlNjB9g_6tRavfVKmuwmO2IHNcZjfdsWM1bkSFko9x4dH5grkH7MPxEHsFpHfnZO0TTA0bnwbEMOfsaszZViiiXsAsHAJFFZx-xqcpTUHVoyP6v0J8bIyhJAHDwOJjYIjxx2c_OGkyLa6-_MMPygw5M24J3H5M4zCQIUO57dnBIrx2uFcB_hX4qivlDAa9Byu0XbvziFaIDxjWlzKiV-r97X2MzsSdgV4meTvRGZ1E9P_Z0W4OqQmxU-azmCrdbL1CqxRE6cciLDwxrKnJ30pLF2Pat8UB7EyM7Cxo_Yiz1YOjoLD-HtdtJtkgADfjkN
    head:
      author: Natalie Jaya
      time: '2026-10-26T13:43:09.894Z'
      body: de manera deliberada!! un administrador decide si ver los tests de manolo69
        i de alli si encuentra algo pues tt
      quote: Moderate tests deliberately
  c9:
    status: ACTIVE
    token: AX5pRSmVkMb8798DSqMwJs58hs15ysdg14qs51UGzycrQGCiOmeoRzNbV8n5ZJGpjFkdB8GgRBPMk3svL9UFksuvMdCmp1-C6kf751ZceJSN9_xNfFKtyDfw-gbT0rH8lLA7h1BnyoMddBoThltAtmArFBxklRUcE3UNeAPXYtK7FuNFb7eUywHdJXW78evVp_KkC-OPusEFUTwT20tiwBCVxKZBLmiLe85QxXRShUbD3zTLJWJtcH-vGM3ro_dEmM8mkRhrb0-u2Ixcy7hmi62bjvyNXOmAEZUZ8elpWE4uFsP1ZZXnT_BAYi50-4vtERuWxteSoSsx816TZ2sZ6o5Co5DAmoNRMVFcHxUgnp2Bh5vqaXfsEKzEMc79duE
    head:
      author: Natalie Jaya
      time: '2026-10-26T13:48:33.350Z'
      body: un estudiante que otro contenido puede crear aparte de los tests, entiendo
        que puede referirse a reportes?
      quote: other content
  c10:
    status: ACTIVE
    token: AX5pRSm7WMwP4nVrxH1Y_jpM9bOQmqVpkZ600wpVvTL7N88XlbGtCDfSFowVOQJGr9YGyOC09b3prM6fHBKMnkmw6O4WbcZZgoL68ouX4-0SBDqnaXHdedqo9WD0jWv0I5A_X4Q2VwmDW0LVF2XfnxenjwQ24XSGfevLGIYdgtZLnZgWb84TcuQjDZS3-ucgXzKk2ls_D9WzHIOGcLpr_W_kYoE3Rh8iTA52erg_kC6dNFd9tK1QmKCmVDK2qcplAV4RRcOGdMyo6YvAEoPVqPG3xPEap-Nd0y0INO8z8O3NZS7l9dB7oNXMJoCQ0BDfefykTYXg2FP4CDvccxzvjmGaNpHofWgs1V89z74CcgZCmHwIjauMN2QP6AySgm-I_wDTU8AbBgP8Lwtnofwnfolc3sOFIo1fuPsPuHrtQAgR6WMCo1dc0g
    head:
      author: Natalie Jaya
      time: '2026-10-26T14:23:31.621Z'
      body: que tipo de estadisticas? Quiero decir, si nos interesa lo basica como
        en software entonces en el usuario deberiamos preguntarle cosas como genero,
        edad etc ?
      quote: statistics
  c11:
    status: RESOLVED
    token: AX5pRSlYzqIXKl4kOe3Frz-pDXbdJwISGN2W0LUCR60Al6vncen3NDJFugpvphZO9C1NOcOiyS7zrtRcj0L136AgpXxGd3FBupokikTE7lYnXFS8WWPMSl-S-7T6oWuwLAxeYzjnvPRHygkD8fqV8ruyw22VVskp7wWfuJQXP3Qaq66_kw1ikoQ9GYB54GxsrRC6vqtxkJHFWqE8dcNtp8UOwahZYeJs7lRXDlXiz6VmDSMufCyaI37C8ylB5FheI7kniYtwCVgCs8_ScINp6n7Wm_sKW0D9KvYPOh5-wsAPPbKQchFc4Dh5F85KoPvTDAD0VmmUZcEHXaS6FcAycd9Zar0_TFOQXw2d0yZnCBAe0D0IWq9lBRZdN51Fml4geGeKRGWBccVC2CGahFMxvaP73D5eUistIhiPdruKrUVVbkE6wDtuW2IhO0fGR6NuZ9P2tDxk4kHoPdoJUWqDC7QNJll3kpDAzBPMHj4jDezD6yr3_q_yOkp2
    head:
      author: Natalie Jaya
      time: '2026-10-29T14:01:00.609Z'
      body: esto me lo leo pero la estrcutura ya le pido a la ia pero los tags tienen
        que ser los que tenemos en el github
    replies:
    - author: Marc Pujol Navajo
      time: '2026-10-29T14:28:49.449Z'
      action: RESOLVE
  c12:
    status: ACTIVE
    token: AX5pRSlQAdddqqbePpHGAosymZXJxXtTVBgAp0WFaql82srqcvhs4474HigRjlkVoyjQ8ENfZhV08IRkMByU7YcFkhcsJd_HQ0vbOZfYISbo0vg7B53tM-NEn_XUoVFIGHyXpSTLMxAhBtff5xkT4Au5SaE_U5aH9wTR6bORbrZ8VD-6uWNb0h9QxknDgD5456WddHyFhDjqsHg2-myS6V9DmBPj2DfSjbr4MuTIkJv0UF2IXnHh_G9beVx1H3nk2Pv1l4TKs7fQEOMjd7Zc7antjGy6
    head:
      author: Natalie Jaya
      time: '2026-10-29T13:59:58.188Z'
      body: lo mismo de antes
      quote: statistics
  c13:
    status: ACTIVE
    token: AX5pRSnccCKSEgMLfS4yWqQful_ZNG460pxW_3IgsSA0cdeHYA7j8czYNXKHF9PudPe63yU3blmXoe2Fh5MpScG9Y1hduADkbRbB7eGW72siN4Jyxt6JljhYvgvZzlhJ8D17vCvQco7_3wvhjD4bzhr9mWCro5nYKuL1qVsiKUaR_RWV9a-Q7umRg8WwOQ4EL34cToerREuYO7Hnlck92tBhkA0ZQsMh5DobAM2BXNAMbpaqpGqUq6oW9o5pW85g4asllI_OMN1TGbPkY-5ehRQWDRMb7DR7lk3_BGCjReP-_ZVZlOM33wNlbMAgxBuppwAPB4ZTd7UPcT2tnCve-67812IFfHPB_DcDdegbw7zMUjrdqM730kHZy875s76plMwK7I6IaGI2YNgBJfO4GvxYSopRa9x6gcWQwA
    head:
      author: Natalie Jaya
      time: '2026-10-29T12:28:28.920Z'
      body: me consta en cierta forma, tengo el report vinculado a test i cuestion,
        entonces si no es eso es general problem
      quote: |-
        Report a test.
        Report a question.
        Report a general problem in the platform.
  c14:
    status: RESOLVED
    token: AX5pRSllosn0FcSRW-O_q3jvG_h3pIZNp3-IvsY8SVj5or6ktuT7aqfXwCPMF2YE4pPRT6vzm6Yo-VjuR2f-Ajn-m3WVva7WCRrevvxsTEHXvPRLasa-9sa6TTJ6Mp9ERVuwqdhvyQsB6j5O4RwI3K0QQam4pxETbuHhM10cPc30ZkfNnqITxW0dk7QDL9M7LoZusOBCoxjXjzQm94ntVi0HKsHRsv59rirrcNczDuvA5ohybk-FcpOwMjg5YPvrQH43I91qSRoZYqbQebtJFyb4sOj7BFf-ypkr5QDW518BAAqY2QVFSuYt5B-7yuxFny3Pd0Mixdslr9zCSJkgGONXu8Kv17_67iUzhnWWleBjT8aV62pmzQE5czGQH_Z-P743UfyGv4E6D541V8Ttg3KaoNN8zR1nmwFUinphG3amtMyBDofUGDVx5YRv-TPkj4biksTXflJo0LDKUcgLT76G
    head:
      author: Natalie Jaya
      time: '2026-10-22T13:32:38.027Z'
      body: entenem com a report lo que son &quot;queixes&quot; dels users
      quote: reported
    replies:
    - author: Marc Pujol Navajo
      time: '2026-10-29T14:29:24.636Z'
      action: RESOLVE
  c15:
    status: ACTIVE
    token: AX5pRSmBuAJYK3BI1eR10DZcaHM1dM_IUUjCI_dYGr375h-GLBaaqyakQ_n8kRcGRnv24fb-M-CBxnn3pLlSQr9WPmkTLQlioUM1BjXjy5bA0xtV7AxMYu7jU1Cma5zbwMwGTC2wrgt9Rsn6xTQhkJYaBMaN-fVf4vbM6rsetck1Ts0yFVlj5dmIZRO9teQci0B1NYEOXZoQQfmW_LL8P6AT9WdH3M0ofiBPiTVP03Je_pq0EjBFXXkCqF8FKkKJhfIYYMPb0JjxoGGgU13nK1L-her1fBPB77IfTC4Ll_t6CESv9CFEmtM81ItTRzQeAxrrtfdGwVCyVw1_MkPQZpja-Ugf858ypLJsGiVtE8ZpjY2zTjO7cR7D0zEv9YRpVFYod9Ne21gi_6mwGvAxlYRfprZyDzHS11OLkrGt5ofzLrKUbGSElohmjBhLwtDrGG3nnZ3EqxfYl_Gbcm42
    head:
      author: Natalie Jaya
      time: '2026-10-29T13:56:09.421Z'
      body: '&quot;, but can be added in the future.&quot; esto estaba puesto pero
        meh yo lo quitaria, ns hablamos factos que esto se lo paso a la ia i no sea
        que la lia'
      quote: ny badge related to custom tests.
---
