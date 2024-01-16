package pt.ipca.hs.Models

data class MensagensGroup(
    val id: Long,
    val sender: String,
    val receiver: String,
    val messages: String
)
{
    override fun toString(): String {
        return "MensagensGroup(id=$id, senders=$sender, receivers=$receiver, messages=$messages)"
    }
}