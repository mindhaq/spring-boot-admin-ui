/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.aetas.springbootadmin;

import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.notify.AbstractStatusChangeNotifier;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import pl.aetas.springbootadmin.notifier.mail.MailgunService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Mail based notifier using Mailgun.net
 */
public class MailgunNotifier extends AbstractStatusChangeNotifier {
	private static final String DEFAULT_SUBJECT = "#{application.name} (#{application.id}) is #{to.status}";
	private static final String DEFAULT_TEXT = "#{application.name} (#{application.id})\nstatus changed from #{from.status} to #{to.status}\n\n#{application.healthUrl}";

	private final SpelExpressionParser parser = new SpelExpressionParser();

	/**
	 * recipients of the mail
	 */
	private final String to;

	/**
	 * sender of the change
	 */
	private final String from;

	/**
	 * Mail Text. SpEL template using event as root;
	 */
	private final Expression text;

	/**
	 * Mail Subject. SpEL template using event as root;
	 */
	private final Expression subject;

	private final MailgunService mailgunService;

	public MailgunNotifier(MailgunService mailgunService, String from, String to)
	{
		checkArgument(mailgunService != null);
		checkArgument(from != null);
		checkArgument(to != null);
		this.mailgunService = mailgunService;
		this.from = from;
		this.to = to;
		this.subject = parser.parseExpression(DEFAULT_SUBJECT, ParserContext.TEMPLATE_EXPRESSION);
		this.text = parser.parseExpression(DEFAULT_TEXT, ParserContext.TEMPLATE_EXPRESSION);
	}

	@Override
	protected void doNotify(ClientApplicationEvent event) {
		EvaluationContext context = new StandardEvaluationContext(event);

		mailgunService.sendPlainTextMail(from, to, subject.getValue(context, String.class), text.getValue(context, String.class));
	}
}
