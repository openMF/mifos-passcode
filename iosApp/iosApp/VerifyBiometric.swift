import Foundation
import SwiftUI

struct VerifyBiometric: View {
    
    @Binding var path: NavigationPath

    var body: some View {
        VStack {
            Image("biometric")
                .resizable()
                .scaledToFit()
            Spacer()
            Button(action: {
                
            }, label: {
                Text("Veify Biometric")
            })
        }.padding()
    }
}
