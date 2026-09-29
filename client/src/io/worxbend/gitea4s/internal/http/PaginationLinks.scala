package io.worxbend.gitea4s.internal.http

/** Reads pagination relations from RFC 8288 Link fields without interpreting targets. */
private[gitea4s] object PaginationLinks:
  def hasNext(headers: Iterable[String]): Boolean =
    headers.iterator.flatMap(splitOutsideValues(_, ',')).exists(hasNextRelation)

  private def hasNextRelation(link: String): Boolean =
    val parts = splitOutsideValues(link, ';')
    parts.headOption.exists(target => target.startsWith("<") && target.endsWith(">")) &&
      parts.drop(1).iterator
        .map(_.split("=", 2))
        .collectFirst { case Array(name, value) if name.trim.equalsIgnoreCase("rel") => value.trim }
        .exists { value =>
          relationTypes(value).exists(_.equalsIgnoreCase("next"))
        }

  private def relationTypes(value: String): List[String] =
    if value.startsWith("\"") && value.endsWith("\"") && value.length >= 2 then
      value.substring(1, value.length - 1).replaceAll("\\\\(.)", "$1").split(" +").toList
    else if value.nonEmpty && !value.exists(_.isWhitespace) && !value.contains('"') then List(value)
    else Nil

  // Commas and semicolons inside URI targets or quoted parameters are data,
  // not separators. A quoted-pair also prevents its quote from ending a value.
  private def splitOutsideValues(value: String, separator: Char): List[String] =
    var parts = List.empty[String]
    var start = 0
    var quoted = false
    var escaped = false
    var target = false
    var index = 0

    while index < value.length do
      val character = value.charAt(index)
      if escaped then escaped = false
      else if quoted && character == '\\' then escaped = true
      else if !target && character == '"' then quoted = !quoted
      else if !quoted then
        if character == '<' then target = true
        else if character == '>' then target = false
        else if !target && character == separator then
          parts = value.substring(start, index).trim :: parts
          start = index + 1
      index += 1

    if quoted || target || escaped then Nil
    else (value.substring(start).trim :: parts).reverse
