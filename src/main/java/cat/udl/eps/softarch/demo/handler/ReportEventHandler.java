package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Report;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.ReportRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RepositoryEventHandler
public class ReportEventHandler {
    final ReportRepository reportRepository;

    public ReportEventHandler(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @HandleBeforeCreate
    public void handleReportPreCreate(Report report) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        report.setReporter(user);
        report.setStatus(Report.Status.PENDING);
    }

    @HandleBeforeSave
    public void handleReportPreSave(Report report) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage reports");
        }
        if ((report.getStatus() == Report.Status.RESOLVED || report.getStatus() == Report.Status.DISMISSED)
                && report.getResolvedAt() == null) {
            report.setResolvedBy(user);
            report.setResolvedAt(Instant.now());
        }
    }

    @HandleBeforeDelete
    public void handleReportPreDelete(Report report) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can delete reports");
        }
    }
}
