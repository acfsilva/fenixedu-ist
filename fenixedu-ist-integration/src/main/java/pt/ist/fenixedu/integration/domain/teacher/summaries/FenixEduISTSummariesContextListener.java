package pt.ist.fenixedu.integration.domain.teacher.summaries;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.OccupationPeriod;
import org.fenixedu.academic.domain.OccupationPeriodType;
import org.fenixedu.academic.domain.Summary;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;

@WebListener
public class FenixEduISTSummariesContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.register(Summary.CREATE_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            Calendar gradeSubmissionEndDate = event.getInstance().getExecutionCourse().getExecutionDegrees().stream()
                    .flatMap(ed -> ed.getPeriods(OccupationPeriodType.GRADE_SUBMISSION)).map(o -> o.getEndDate())
                    .max(Calendar::compareTo).get();
            
            if (gradeSubmissionEndDate.before(Calendar.getInstance())) {
                throw new DomainException("error.summary.current.date.after.end.period");
            }
        });

        Signal.register(Summary.EDIT_SIGNAL, (DomainObjectEvent<Summary> event) -> {
            System.out.println("Do something here");
        });
    }

    private void handle(Summary s) {

        Calendar testDate = s.getExecutionCourse().getExecutionDegrees().iterator().next()
                .getPeriods(OccupationPeriodType.GRADE_SUBMISSION)
                .iterator().next().getEndDate();
       


        Stream<ExecutionDegree> eds = s.getExecutionCourse().getExecutionDegrees().stream();
        Stream<OccupationPeriod> op = eds.flatMap(ed -> ed.getPeriods(OccupationPeriodType.GRADE_SUBMISSION));
        List<Calendar> endDates = op.map(o -> o.getEndDate()).collect(Collectors.toList());
       
        Calendar endDate = endDates.stream().max(Calendar::compareTo).get();
        
        Calendar maxEndDate = s.getExecutionCourse().getExecutionDegrees().stream()
                .flatMap(ed -> ed.getPeriods(OccupationPeriodType.GRADE_SUBMISSION))
                .map(o -> o.getEndDate()).max(Calendar::compareTo).get();
        
        if(maxEndDate.before(Calendar.getInstance())) {
            throw new DomainException("message.error.page.description");
        }
        


    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }
}
