import SwiftUI
import Compox2DiOS

struct ContentView: View {
    var body: some View {
        GameViewController()
            .ignoresSafeArea()
    }
}

private struct GameViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
