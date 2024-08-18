import Foundation
import SwiftUI

struct SetPublicKey: View {
    
    @Binding var path: NavigationPath

    var body: some View {
        VStack {
            Image("biometric")
                .resizable()
                .scaledToFit()
            Spacer()
            Button(action: {
                
            }, label: {
                Text("Set Biometric")
            })
        }.padding()
    }
}
