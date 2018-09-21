package chat.rocket.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import chat.rocket.android.util.extension.orFalse
import chat.rocket.core.model.attachment.Attachment
import chat.rocket.core.model.attachment.AudioAttachment
import chat.rocket.core.model.attachment.AuthorAttachment
import chat.rocket.core.model.attachment.ColorAttachment
import chat.rocket.core.model.attachment.ImageAttachment
import chat.rocket.core.model.attachment.MessageAttachment
import chat.rocket.core.model.attachment.VideoAttachment
import timber.log.Timber

@Entity(tableName = "attachments",
        foreignKeys = [
            ForeignKey(entity = MessageEntity::class, parentColumns = ["id"],
                    childColumns = ["message_id"], onDelete = ForeignKey.CASCADE)
        ])
data class AttachmentEntity(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "message_id")
    val messageId: String,
    val title: String? = null,
    val type: String? = null,
    val description: String? = null,
    val text: String? = null,
    @ColumnInfo(name = "author_name")
    val authorName: String? = null,
    @ColumnInfo(name = "author_icon")
    val authorIcon: String? = null,
    @ColumnInfo(name = "author_link")
    val authorLink: String? = null,
    @ColumnInfo(name = "thumb_url")
    val thumbUrl: String? = null,
    val color: String? = null,
    @ColumnInfo(name = "title_link")
    val titleLink: String? = null,
    @ColumnInfo(name = "title_link_download")
    val titleLinkDownload: Boolean = false,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    @ColumnInfo(name = "image_type")
    val imageType: String? = null,
    @ColumnInfo(name = "image_size")
    val imageSize: Long? = null,
    @ColumnInfo(name = "video_url")
    val videoUrl: String? = null,
    @ColumnInfo(name = "video_type")
    val videoType: String? = null,
    @ColumnInfo(name = "video_size")
    val videoSize: Long? = null,
    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,
    @ColumnInfo(name = "audio_type")
    val audioType: String? = null,
    @ColumnInfo(name = "audio_size")
    val audioSize: Long? = null,
    @ColumnInfo(name = "message_link")
    val messageLink: String? = null,
    val timestamp: Long? = null
) : BaseMessageEntity

@Entity(tableName = "attachment_fields",
        foreignKeys = [
            ForeignKey(entity = AttachmentEntity::class, parentColumns = ["id"],
                    childColumns = ["attachmentId"], onDelete = ForeignKey.CASCADE)
        ])
data class AttachmentFieldEntity(
    val attachmentId: String,
    val title: String,
    val value: String
) : BaseMessageEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}

fun Attachment.asEntity(msgId: String): List<BaseMessageEntity> {
    return when(this) {
        is ImageAttachment -> listOf(asEntity(msgId))
        is VideoAttachment -> listOf(asEntity(msgId))
        is AudioAttachment -> listOf(asEntity(msgId))
        is AuthorAttachment -> asEntity(msgId)
        is ColorAttachment -> listOf(asEntity(msgId))
        is MessageAttachment -> listOf(asEntity(msgId))
        // TODO - Action Attachments
        else -> {
            Timber.d("Missing conversion for: ${javaClass.canonicalName}")
            emptyList()
        }
    }
}

fun ImageAttachment.asEntity(msgId: String): AttachmentEntity =
    AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        title = title,
        description =  description,
        text = text,
        titleLink = titleLink,
        titleLinkDownload = titleLinkDownload.orFalse(),
        imageUrl = url,
        imageType = type,
        imageSize = size
    )

fun VideoAttachment.asEntity(msgId: String): AttachmentEntity =
    AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        title = title,
        description =  description,
        text = text,
        titleLink = titleLink,
        titleLinkDownload = titleLinkDownload.orFalse(),
        videoUrl = url,
        videoType = type,
        videoSize = size
    )

fun AudioAttachment.asEntity(msgId: String): AttachmentEntity =
    AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        title = title,
        description =  description,
        text = text,
        titleLink = titleLink,
        titleLinkDownload = titleLinkDownload.orFalse(),
        audioUrl = url,
        audioType = type,
        audioSize = size
    )

fun AuthorAttachment.asEntity(msgId: String): List<BaseMessageEntity> {
    val list = mutableListOf<BaseMessageEntity>()
    val attachment = AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        authorLink = url,
        authorIcon = authorIcon,
        authorName = authorName
    )
    list.add(attachment)

    fields?.forEach { field ->
        val entity = AttachmentFieldEntity(
            attachmentId = attachment.id,
            title = field.title,
            value = field.value
        )
        list.add(entity)
    }

    return list
}

fun ColorAttachment.asEntity(msgId: String): AttachmentEntity =
    AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        color = color.rawColor
    )

// TODO - how to model An message attachment with attachments???
fun MessageAttachment.asEntity(msgId: String): AttachmentEntity =
    AttachmentEntity(
        id = "${msgId}_${hashCode()}",
        messageId = msgId,
        authorName = author,
        authorIcon = icon,
        text = text,
        thumbUrl = thumbUrl,
        color = color?.rawColor,
        messageLink = url,
        timestamp = timestamp
    )