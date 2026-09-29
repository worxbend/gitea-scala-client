package io.worxbend.gitea4s.model.contract

enum AdminHookType(val value: String):
  case System extends AdminHookType("system")
  case Default extends AdminHookType("default")
  case All extends AdminHookType("all")

enum PackageType(val value: String):
  case Alpine extends PackageType("alpine")
  case Cargo extends PackageType("cargo")
  case Chef extends PackageType("chef")
  case Composer extends PackageType("composer")
  case Conan extends PackageType("conan")
  case Conda extends PackageType("conda")
  case Container extends PackageType("container")
  case Cran extends PackageType("cran")
  case Debian extends PackageType("debian")
  case Generic extends PackageType("generic")
  case Go extends PackageType("go")
  case Helm extends PackageType("helm")
  case Maven extends PackageType("maven")
  case Npm extends PackageType("npm")
  case Nuget extends PackageType("nuget")
  case Pub extends PackageType("pub")
  case Pypi extends PackageType("pypi")
  case Rpm extends PackageType("rpm")
  case Rubygems extends PackageType("rubygems")
  case Swift extends PackageType("swift")
  case Terraform extends PackageType("terraform")
  case Vagrant extends PackageType("vagrant")

enum IssueSearchState(val value: String):
  case Open extends IssueSearchState("open")
  case Closed extends IssueSearchState("closed")
  case All extends IssueSearchState("all")

enum IssueSearchType(val value: String):
  case Issues extends IssueSearchType("issues")
  case Pulls extends IssueSearchType("pulls")

enum NotificationSubjectType(val value: String):
  case Issue extends NotificationSubjectType("issue")
  case Pull extends NotificationSubjectType("pull")
  case Commit extends NotificationSubjectType("commit")
  case Repository extends NotificationSubjectType("repository")
