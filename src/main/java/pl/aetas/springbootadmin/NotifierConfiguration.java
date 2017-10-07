package pl.aetas.springbootadmin;

import com.google.common.collect.Sets;
import de.codecentric.boot.admin.notify.CompositeNotifier;
import de.codecentric.boot.admin.notify.LoggingNotifier;
import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.aetas.springbootadmin.notifier.mail.MailgunService;

/**
 * @author gelder
 */
@Configuration
@EnableScheduling
public class NotifierConfiguration
{
	@Autowired
	private MailgunService mailgunService;

	@Value("${spring.boot.admin.notify.mail.enabled}")
	private boolean mailEnabled;
	@Value("${spring.boot.admin.notify.mail.from}")
	private String from;
	@Value("${spring.boot.admin.notify.mail.to}")
	private String to;
	@Value("${spring.boot.admin.notify.reminder.period}")
	private int reminderPeriodInMs;

	@Bean
	public FilteringNotifier filteringNotifier(Notifier delegate)
	{
		return new FilteringNotifier(delegate);
	}

	@Bean
	public LoggingNotifier loggerNotifier()
	{
		return new LoggingNotifier();
	}

	@Bean
	@ConditionalOnProperty("spring.boot.admin.notify.mail.enabled")
	public MailgunNotifier mailgunNotifier()
	{
		return new MailgunNotifier(mailgunService, from, to);
	}

	@Bean
	@Primary
	public RemindingNotifier remindingNotifier()
	{
		Notifier delegate = loggerNotifier();
		if(mailEnabled)
		{
			delegate = new CompositeNotifier(Sets.newHashSet(loggerNotifier(), mailgunNotifier()));
		}
		RemindingNotifier notifier = new RemindingNotifier(filteringNotifier(delegate));
		notifier.setReminderPeriod(reminderPeriodInMs);
		return notifier;
	}

	@Scheduled(fixedRateString = "${spring.boot.admin.notify.rate}")
	public void remind()
	{
		remindingNotifier().sendReminders();
	}
}
