package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * @author Artur Bosch
 */
class TooGenericExceptionCaught(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Thrown exception is too generic. " +
					"Prefer throwing project specific exceptions to handle error cases.")

	private val exceptions: Set<String> = valueOrDefault(CAUGHT_EXCEPTIONS_PROPERTY, CAUGHT_EXCEPTIONS).toHashSet()

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchParameter?.let {
			val text = it.typeReference?.text
			if (text != null && text in exceptions)
				report(CodeSmell(issue, Entity.from(it)))
		}
		super.visitCatchSection(catchClause)
	}

	companion object {
		const val CAUGHT_EXCEPTIONS_PROPERTY = "exceptions"
	}
}

val CAUGHT_EXCEPTIONS = listOf(
		"ArrayIndexOutOfBoundsException",
		"Error",
		"Exception",
		"IllegalMonitorStateException",
		"InterruptedException",
		"NullPointerException",
		"IndexOutOfBoundsException",
		"RuntimeException",
		"Throwable"
)
