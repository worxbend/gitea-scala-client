package io.worxbend.gitea4s.model

import zio.Chunk

/** File bytes and the filename sent in a multipart attachment upload. */
final case class AttachmentUpload(fileName: String, content: Chunk[Byte])
