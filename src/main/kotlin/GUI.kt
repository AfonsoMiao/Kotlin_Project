import Classes.Injector
import Classes.Window

fun main() {
    val w = Injector().create(Window::class)
    w.open()
}