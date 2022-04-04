//
//  ObservableViewModel.swift
//  iosApp
//
//  Created by Brandon Niedert on 2/22/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

class ObservableViewModel<S: ViewState, A: Action, ViewModel: BaseViewModel<S, A>>: ObservableObject {
    public private(set) var viewModel: ViewModel
    @Published var state: S
    
    var stateWatcher : Closeable?

    init(sharedViewModel: ViewModel) {
        viewModel = sharedViewModel
        state = viewModel.initialState()
        stateWatcher = viewModel.watchState().watch { [weak self] state in
            guard let self = self else { return }
            withAnimation {
                self.state = state
            }
        }
    }

    func onAction(action: A) {
        viewModel.onAction(action: action)
    }

    deinit {
        stateWatcher?.close()
    }
}
