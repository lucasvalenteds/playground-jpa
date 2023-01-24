package io.playground;

import io.playground.attendance.AttendanceService;
import io.playground.issue.Issue;
import io.playground.issue.IssueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigurePostgresDatabase
class LockPessimisticWriteTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private AttendanceService attendanceService;

    @AfterEach
    public void afterEach() {
        attendanceService.removeAssignment(1L);
        attendanceService.removeAssignment(2L);
    }

    @RepeatedTest(10)
    void twoAccountsDoesNotAttendTheSameIssue() throws InterruptedException {
        final var johnSmithAccountId = 1L;
        final var maryJaneAccountId = 2L;

        final var executorService = Executors.newFixedThreadPool(2);
        final Callable<Issue> callable1 = () -> attendanceService.assignNextIssue(johnSmithAccountId);
        final Callable<Issue> callable2 = () -> attendanceService.assignNextIssue(maryJaneAccountId);
        executorService.invokeAll(List.of(callable1, callable2));

        // select i1_0.id,i1_0.account_id,i1_0.created_at,i1_0.title from issue i1_0 where i1_0.id=?
        // select a1_0.id,a1_0.username from account a1_0 where a1_0.id=?
        final var issue1 = issueRepository.findById(1L).orElseThrow();
        final var issue2 = issueRepository.findById(2L).orElseThrow();
        assertThat(issue1.getAccount()).isNotNull();
        assertThat(issue2.getAccount()).isNotNull();
        assertThat(issue1.getAccount().getId()).isNotEqualTo(issue2.getAccount().getId());
    }
}
