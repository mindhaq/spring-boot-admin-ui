package pl.aetas.springbootadmin;

import de.codecentric.boot.admin.notify.LoggingNotifier;
import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * @author gelder
 */
@Configuration
@EnableScheduling
public class NotifierConfiguration
{
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
	@Primary
	public RemindingNotifier remindingNotifier()
	{
		RemindingNotifier notifier = new RemindingNotifier(filteringNotifier(loggerNotifier()));
		notifier.setReminderPeriod(TimeUnit.SECONDS.toMillis(10));
		return notifier;
	}

	@Scheduled(fixedRateString = "${spring.boot.admin.notify.rate}")
	public void remind()
	{
		remindingNotifier().sendReminders();
	}
}
