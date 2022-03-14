package tasks

import contributors.*

val utmRepos = listOf(
    "asprid-flutter",
    "utm-simulator-planner-node",
    "asprid_configurator",
    "asprid-server",
    "eARTS",
    "automated-workflow",
    "asprid-file-storage",
    "utm-engine-server",
    "utm-realtime-ui"
)

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList().filter { repo-> utmRepos.contains(repo.name) }

    var allUsers = emptyList<User>()
    for ((index, repo) in repos.withIndex()) {
        val users = service.getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()

        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, index == repos.lastIndex)
    }
}
