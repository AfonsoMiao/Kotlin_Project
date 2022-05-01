@XmlName("room")
data class Room(
    @XmlIgnore
    val name: String,
    @XmlTagContent
    val capacity: Int,
    @XmlTagContent
    val type: RoomType? = null,
    @XmlTagContent
    var listStudent: List<Student> = emptyList()
)

enum class RoomType {
    Small, Medium, Big
}
