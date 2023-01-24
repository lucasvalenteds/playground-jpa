package io.playground.attendance;

import io.playground.account.AccountRepository;
import io.playground.issue.Issue;
import io.playground.issue.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AccountRepository accountRepository;
    private final IssueRepository issueRepository;

    @Transactional
    public Issue assignNextIssue(final Long accountId) {
        log.info("Finding next issue for account (accountId={})", accountId);

        // select a1_0.id,a1_0.username from account a1_0 where a1_0.id=?
        final var account = accountRepository.getReferenceById(accountId);

        /*
            select i1_0.id,i1_0.account_id,i1_0.created_at,i1_0.title
            from issue i1_0 where i1_0.account_id is null
            order by i1_0.created_at asc offset ?
            rows fetch first ? rows only for no key update

            select count(i1_0.id) from issue i1_0 where i1_0.account_id is null
         */
        final var issue = issueRepository.findAll(nextUnassignedIssue(), Pageable.ofSize(1))
                .stream()
                .findFirst()
                .orElseThrow();

        log.info("Found one issue (accountId={}, issueId={})", accountId, issue.getId());
        issue.setAccount(account);

        // update issue set account_id=?, created_at=?, title=? where id=?
        issueRepository.saveAndFlush(issue);

        log.info("Issue assigned to account (accountId={}, issueId={})", accountId, issue.getId());
        return issue;
    }

    @Transactional
    public void removeAssignment(final Long issueId) {
        log.info("Finding issue by ID (issueId={})", issueId);

        // select i1_0.id,i1_0.account_id,i1_0.created_at,i1_0.title from issue i1_0 where i1_0.id=?
        final var issue = issueRepository.findById(issueId)
                .orElseThrow();

        issue.setAccount(null);

        // update issue set account_id=?, created_at=?, title=? where id=?
        issueRepository.saveAndFlush(issue);

        log.info("Account unassigned from issue (issueId={})", issueId);
    }

    private static Specification<Issue> nextUnassignedIssue() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
            return criteriaBuilder.isNull(root.get("account").get("id"));
        };
    }
}
