import SwiftUI
import shared

let biometricUtil = BiometricUtilIosImpl()
struct ContentView: View {
	let greet = Greeting().greet()
    @State private var path = NavigationPath()

	var body: some View {
        ZStack {
            ComposeViewController(onNavigateToNextScreen: {
                path.append("NextScreen")
            })
        }
        .navigationDestination(for: String.self) { value in
            if value == "NextScreen" {
                NextScreen()
            }
        }
	}
}

struct ComposeViewController: UIViewControllerRepresentable {
    @StateObject var biometricAuthorizationViewModel: BiometricAuthorizationViewModel = BiometricAuthorizationViewModel()
    var onNavigateToNextScreen: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return App_iosKt.MainViewController(
            bioMetricUtil: biometricUtil,
            biometricViewModel: biometricAuthorizationViewModel,
            onPasscodeConfirm: onNavigateToNextScreen
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct NextScreen: View {
    var body: some View {
        Text("Next Screen")
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}

extension BiometricAuthorizationViewModel: ObservableObject {}
