package Data

import Annotations.XmlIgnore
import Annotations.XmlName
import Annotations.XmlTagContent
import Enumerations.RoomType

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
