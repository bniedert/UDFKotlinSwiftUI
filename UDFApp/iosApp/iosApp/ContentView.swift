import SwiftUI
import shared

struct ContentView: View {
    @StateObject var viewModel = ObservableViewModel(sharedViewModel: ContentViewModel())
    @State private var name = ""
    
	var body: some View {
        VStack {
            Text("Age Guesser!")
            TextField("Enter a name", text: $name)
                .padding(EdgeInsets(top: 16, leading: 25, bottom: 0, trailing: 25))
                .textFieldStyle(.roundedBorder)
            Button("Send", action: {
                viewModel.onAction(action: .RequestName(name: name))
            })
                .padding(.bottom, 20)
            
            ForEach(viewModel.state.responses, id: \.self) { response in
                HStack {
                    Text(response.displayString()).multilineTextAlignment(.leading)
                    Spacer()
                }
                .padding(EdgeInsets(top: 4, leading: 25, bottom: 0, trailing: 25))
            }
            
            switch (viewModel.state.loadingState) {
            case .loading:
                ProgressView()
                    .padding(.top, 25)
            default:
                EmptyView()
            }
            
            Spacer()
        }
        .padding(.top, 25)
	}
}
