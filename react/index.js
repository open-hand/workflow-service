import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { ModalContainer } from 'choerodon-ui/pro';
import { stores, nomatch } from '@choerodon/boot';
import 'moment/locale/zh-cn';
import 'moment/locale/en-nz';
import moment from 'moment';
import { AgileProvider } from '@choerodon/agile';
import { setHistory } from '@choerodon/agile/lib/utils/to';
import Config from '@/routes/config';

const { AppState } = stores;

class Index extends React.Component {
  constructor(props) {
    super(props);
    setHistory(props.history);
  }

  // componentDidCatch(error, info) {
  //   Choerodon.prompt(error.message);
  // }
  render() {
    const { match } = this.props;
    const language = AppState.currentLanguage;
    if (language === 'zh_CN') {
      moment.locale('zh-cn');
    }
    return (
      <div id="agile">
        <AgileProvider projectId={AppState.currentMenuType?.id}>
          <>
            <Switch>
              {/* 协作 */}
              <Route path={`${match.url}/config`} component={Config} />
              <Route path="*" component={nomatch} />
            </Switch>
            <ModalContainer />
          </>
        </AgileProvider>
      </div>
    );
  }
}

export default Index;
